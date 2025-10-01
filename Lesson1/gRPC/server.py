from concurrent import futures
import logging

import grpc
import service_pb2
import service_pb2_grpc

class StudentLMS(service_pb2_grpc.StudentLMS):
    def GetAllGrade(self, request, context):
        # Get all grade from database here
        return service_pb2.AllGradeReply()
    
    def GetStat(self, request, conteext):
        # Get stat results
        return service_pb2.StatRepply(

        )

class FakeDatabaseConnection():
    def __init__(self):
        
def serve():
