from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
from typing import List
from sqlalchemy import Column, Integer, String, Text, create_engine, ForeignKey
from sqlalchemy.orm import declarative_base, sessionmaker, relationship, Session
from fastapi import Body
from fastapi.openapi.utils import get_openapi
import yaml

SQLALCHEMY_DATABASE_URL = "sqlite:///./books.db"
engine = create_engine(SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

class BookMetadataORM(Base):
    __tablename__ = "book_metadata"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, index=True)
    tags = Column(String, nullable=True) 
    description = Column(Text)
    author = Column(String)
    content = relationship("BookContentORM", back_populates="book", uselist=False)

class BookContentORM(Base):
    __tablename__ = "book_content"
    id = Column(Integer, ForeignKey("book_metadata.id"), primary_key=True)
    content = Column(Text)
    book = relationship("BookMetadataORM", back_populates="content")

Base.metadata.create_all(bind=engine)

# ==================================

class BookMetadata(BaseModel):
    id: int
    name: str
    tags: List[str]
    description: str
    author: str

    class Config:
        orm_mode = True

class BookContent(BaseModel):
    id: int
    content: str

    class Config:
        orm_mode = True

app = FastAPI()

with open("custom_doc.yaml", "r") as f:
    custom_openapi = yaml.safe_load(f)

def custom_openapi_func():
    return custom_openapi

app.openapi = custom_openapi_func

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.get("/books/", response_model=List[BookMetadata])
def list_books(db: Session = Depends(get_db)):
    books = db.query(BookMetadataORM).all()
    return [
        BookMetadata(
            id=b.id,
            name=b.name,
            tags=b.tags.split(",") if b.tags else [],
            description=b.description,
            author=b.author
        ) for b in books
    ]

@app.get("/books/by-tag/{tag}", response_model=List[BookMetadata])
def get_books_by_tag(tag: str, db: Session = Depends(get_db)):
    books = db.query(BookMetadataORM).all()
    filtered = [b for b in books if tag in (b.tags.split(",") if b.tags else [])]
    if not filtered:
        raise HTTPException(status_code=404, detail="No books found with this tag")
    return [
        BookMetadata(
            id=b.id,
            name=b.name,
            tags=b.tags.split(",") if b.tags else [],
            description=b.description,
            author=b.author
        ) for b in filtered
    ]

@app.get("/books/by-name/{name}", response_model=List[BookMetadata])
def get_books_by_name(name: str, db: Session = Depends(get_db)):
    books = db.query(BookMetadataORM).filter(BookMetadataORM.name.ilike(name)).all()
    if not books:
        raise HTTPException(status_code=404, detail="No books found with this name")
    return [
        BookMetadata(
            id=b.id,
            name=b.name,
            tags=b.tags.split(",") if b.tags else [],
            description=b.description,
            author=b.author
        ) for b in books
    ]

@app.post("/books/add", response_model=BookMetadata)
def add_book(
    metadata: BookMetadata = Body(...), 
    content: BookContent = Body(...), 
    db: Session = Depends(get_db)
):
    if db.query(BookMetadataORM).filter(BookMetadataORM.id == metadata.id).first():
        raise HTTPException(status_code=400, detail="Book with this ID already exists")

    tags_str = ",".join(metadata.tags)
    new_book = BookMetadataORM(
        id=metadata.id,
        name=metadata.name,
        tags=tags_str,
        description=metadata.description,
        author=metadata.author
    )
    db.add(new_book)

    new_content = BookContentORM(id=metadata.id, content=content.content)
    new_book.content = new_content
    db.add(new_content)

    db.commit()
    db.refresh(new_book)
    return metadata

@app.post("/books/add", response_model=BookMetadata)
def add_book(book: BookMetadata, db: Session = Depends(get_db)):
    if db.query(BookMetadataORM).filter(BookMetadataORM.id == book.id).first():
        raise HTTPException(status_code=400, detail="Book with this ID already exists")
    tags_str = ",".join(book.tags)
    new_book = BookMetadataORM(
        id=book.id,
        name=book.name,
        tags=tags_str,
        description=book.description,
        author=book.author
    )
    db.add(new_book)
    db.commit()
    db.refresh(new_book)
    return book

@app.put("/books/update/{id}", response_model=BookMetadata)
def update_book(id: int, updated: BookMetadata, content: BookContent, db: Session = Depends(get_db)):
    book = db.query(BookMetadataORM).filter(BookMetadataORM.id == id).first()
    if not book:
        raise HTTPException(status_code=404, detail="Book not found")

    book.name = updated.name
    book.tags = ",".join(updated.tags)
    book.description = updated.description
    book.author = updated.author

    if book.content:
        book.content.content = content.content
    else:
        new_content = BookContentORM(id=id, content=content.content)
        book.content = new_content
        db.add(new_content)

    db.commit()
    db.refresh(book)
    return updated

@app.delete("/books/remove/{id}")
def remove_book(id: int, db: Session = Depends(get_db)):
    book = db.query(BookMetadataORM).filter(BookMetadataORM.id == id).first()
    if not book:
        raise HTTPException(status_code=404, detail="Book not found")

    if book.content:
        db.delete(book.content)

    db.delete(book)
    db.commit()
    return {"message": f"Book {id} and its content removed"}
