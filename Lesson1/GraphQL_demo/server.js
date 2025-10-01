const express = require('express');
const { graphqlHTTP } = require('express-graphql');
const { buildSchema } = require('graphql');

const schema = buildSchema(`
  type Mark {
    subject: String!
    mark10: Float!     
    credit: Int!
    gpa4: Float!
    letter: String!
  }

  type Student {
    id: ID!
    name: String!
    age: Int
    major: String
    marks: [Mark!]!
    totalCredits: Int!
    gpa: Float!
    rank: String!
  }

  input MarkInput {
    subject: String!
    mark10: Float!
    credit: Int!
  }

  input AddStudentInput {
    name: String!
    age: Int
    major: String
    marks: [MarkInput!]!
  }

  type Query {
    students: [Student!]!
    student(id: ID!): Student
    studentsByMajor(major: String!): [Student!]!
    topStudents(limit: Int = 3): [Student!]!
  }

  type Mutation {
    addStudent(input: AddStudentInput!): Student
  }
`);

const studentsDb = [
  {
    id: '1',
    name: 'Nguyễn Văn A',
    age: 19,
    major: 'CNTT',
    marks: [
      { subject: 'Giai tich 1', mark10: 8.5, credit: 4 },
      { subject: 'Vat ly dai cuong', mark10: 9.2, credit: 3 },
      { subject: 'Tieng Anh B1', mark10: 7.8, credit: 5 }
    ]
  },
  {
    id: '2',
    name: 'Trần Thị B',
    age: 20,
    major: 'KHMT',
    marks: [
      { subject: 'Lap trinh OOP', mark10: 8.8, credit: 3 },
      { subject: 'Khoa hoc du lieu', mark10: 7.4, credit: 3 },
      { subject: 'Tieng Anh B2', mark10: 9.0, credit: 5 }
    ]
  },
  {
    id: '3',
    name: 'Lê Văn C',
    age: 21,
    major: 'TTNT',
    marks: [
      { subject: 'Hoc sau', mark10: 6.5, credit: 3 },
      { subject: 'Xu ly ngon ngu tu nhien', mark10: 7.5, credit: 4 },
      { subject: 'AI', mark10: 8.5, credit: 2 }
    ]
  }
];

function convert10to4(mark) {
  if (mark >= 9) return { letter: 'A+', gpa: 4.0 };
  if (mark >= 8.5) return { letter: 'A', gpa: 3.7 };
  if (mark >= 8) return { letter: 'B+', gpa: 3.5 };
  if (mark >= 7) return { letter: 'B', gpa: 3.0 };
  if (mark >= 6.5) return { letter: 'C+', gpa: 2.5 };
  if (mark >= 6) return { letter: 'C', gpa: 2.0 };
  if (mark >= 5) return { letter: 'D+', gpa: 1.5 };
  if (mark >= 4) return { letter: 'D', gpa: 1.0 };
  return { letter: 'F', gpa: 0.0 };
}

function totalCreditsOf(student) {
  return student.marks.reduce((s, m) => s + (m.credit || 0), 0);
}

function calcGPA(student) {
  const totalCredits = totalCreditsOf(student);
  if (!totalCredits) return 0;

  const sum = student.marks.reduce((s, m) => {
    const conv = convert10to4(m.mark10);
    console.log(m.subject, m.mark10, '->', conv);
    return s + conv.gpa * (m.credit || 0);
  }, 0);
  return Math.round((sum / totalCredits) * 100) / 100;
}

function rankFromGPA(gpa) {
  if (gpa >= 3.6) return 'Xuất sắc';
  if (gpa >= 3.2) return 'Giỏi';
  if (gpa >= 2.5) return 'Khá';
  if (gpa >= 2.0) return 'Trung bình';
  return 'Yếu';
}

function enrichStudent(student) {
  const totalCredits = totalCreditsOf(student);
  const gpa = calcGPA(student);
  const rank = rankFromGPA(gpa);
  const marks = student.marks.map(m => {
    const conv = convert10to4(m.mark10);
    return { ...m, gpa4: conv.gpa, letter: conv.letter };
  });
  return { ...student, totalCredits, gpa, rank, marks };
}

const root = {
  students: () => studentsDb.map(enrichStudent),

  student: ({ id }) => {
    const s = studentsDb.find(st => st.id === id);
    return s ? enrichStudent(s) : null;
  },

  studentsByMajor: ({ major }) => studentsDb.filter(s => s.major === major).map(enrichStudent),

  topStudents: ({ limit = 3 }) => studentsDb
    .map(enrichStudent)
    .sort((a, b) => b.gpa - a.gpa)
    .slice(0, limit),

  addStudent: ({ input }) => {
    const newId = String(studentsDb.length + 1);
    const newStudent = { id: newId, ...input };
    studentsDb.push(newStudent);
    return enrichStudent(newStudent);
  }
};

const app = express();

app.use('/graphql', graphqlHTTP({
  schema: schema,
  rootValue: root,
  graphiql: true,
}));

app.listen(2345, () => {
  console.log('Running a GraphQL API server at http://localhost:2345/graphql');
});

// Lấy đúng dữ liệu cần, không dư thừa
// {
//   students {
//     name
//     gpa
//   }
// }




// Gom nhiều dữ liệu liên quan trong 1 query
// {
//   student(id: "1") {
//     name
//     major
//     marks {
//       subject
//       mark10
//       gpa4
//       letter
//     }
//     gpa
//     rank
//   }
// }


// Truy vấn chức năng
// topStudents(limit: 2) {
//     name
//     gpa
//   }



// Mutation: vừa ghi vừa đọc trong cùng một query
// mutation {
//   addStudent(input: {
//     name: "Phạm Văn D"
//     age: 20
//     major: "CNTT"
//     marks: [
//       { subject: "CSDL", mark10: 8.0, credit: 3 }
//       { subject: "Nguyen ly he dieu hanh", mark10: 7.5, credit: 4 }
//     ]
//   }) {
//     id
//     name
//     gpa
//     rank
//     marks {
//       subject
//       letter
//       gpa4
//     }
//   }
// }