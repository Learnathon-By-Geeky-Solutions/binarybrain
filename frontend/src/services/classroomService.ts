import api from './api';
import { Classroom, CreateClassroomDto, UpdateClassroomDto } from '../interfaces/classroom';

export const classroomService = {
    // Get all classrooms (admin only)
    getAllClassrooms: async (): Promise<Classroom[]> => {
        const response = await api.get<Classroom[]>('/api/v1/private/classroom');
        return response.data;
    },

    // Get classroom by ID
    getClassroomById: async (id: number): Promise<Classroom> => {
        const response = await api.get<Classroom>(`/api/v1/private/classroom/${id}`);
        return response.data;
    },

    // Get classrooms by teacher ID
    getClassroomsByTeacher: async (teacherId: number): Promise<Classroom[]> => {
        const response = await api.get<Classroom[]>(`/api/v1/private/classroom/by-teacher/${teacherId}`);
        return response.data;
    },

    // Get classrooms by student ID
    getClassroomsByStudent: async (studentId: number): Promise<Classroom[]> => {
        const response = await api.get<Classroom[]>(`/api/v1/private/classroom/by-student/${studentId}`);
        return response.data;
    },

    // Create new classroom
    createClassroom: async (classroomData: CreateClassroomDto): Promise<Classroom> => {
        const response = await api.post<Classroom>('/api/v1/private/classroom', classroomData);
        return response.data;
    },

    // Update classroom
    updateClassroom: async (id: number, classroomData: UpdateClassroomDto): Promise<Classroom> => {
        const response = await api.put<Classroom>(`/api/v1/private/classroom/${id}`, classroomData);
        return response.data;
    },

    // Delete classroom
    deleteClassroom: async (id: number): Promise<void> => {
        await api.delete(`/api/v1/private/classroom/${id}`);
    },

    // Add student to classroom
    addStudent: async (classroomId: number, studentId: number): Promise<Classroom> => {
        const response = await api.put<Classroom>(
            `/api/v1/private/classroom/${classroomId}/add-student/${studentId}`
        );
        return response.data;
    },

    // Remove student from classroom
    removeStudent: async (classroomId: number, studentId: number): Promise<Classroom> => {
        const response = await api.delete<Classroom>(
            `/api/v1/private/classroom/${classroomId}/remove-student/${studentId}`
        );
        return response.data;
    },

    // Add course to classroom
    addCourse: async (classroomId: number, courseId: number): Promise<Classroom> => {
        const response = await api.put<Classroom>(
            `/api/v1/private/classroom/${classroomId}/add-course/${courseId}`
        );
        return response.data;
    },

    // Remove course from classroom
    removeCourse: async (classroomId: number, courseId: number): Promise<Classroom> => {
        const response = await api.delete<Classroom>(
            `/api/v1/private/classroom/${classroomId}/remove-course/${courseId}`
        );
        return response.data;
    },

    // Get classroom courses
    getClassroomCourses: async (classroomId: number): Promise<any[]> => {
        const response = await api.get<any[]>(`/api/v1/private/classroom/${classroomId}/courses`);
        return response.data;
    }
};