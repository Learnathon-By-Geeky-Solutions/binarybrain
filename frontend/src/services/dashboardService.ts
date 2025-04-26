import api from './api';
import { Course, Classroom, Task } from '../interfaces/dashboard';

export const dashboardService = {
    // Course-related methods
    getCourses: async (): Promise<Course[]> => {
        const response = await api.get<Course[]>('/api/v1/private/course');
        return response.data;
    },

    getCoursesByAuthor: async (authorId: number): Promise<Course[]> => {
        const response = await api.get<Course[]>(`/api/v1/private/course/author/${authorId}`);
        return response.data;
    },

    // Classroom-related methods
    getClassrooms: async (): Promise<Classroom[]> => {
        const response = await api.get<Classroom[]>('/api/v1/private/classroom');
        return response.data;
    },

    getClassroomsByTeacher: async (teacherId: number): Promise<Classroom[]> => {
        const response = await api.get<Classroom[]>(`/api/v1/private/classroom/by-teacher/${teacherId}`);
        return response.data;
    },

    getClassroomsByStudent: async (studentId: number): Promise<Classroom[]> => {
        const response = await api.get<Classroom[]>(`/api/v1/private/classroom/by-student/${studentId}`);
        return response.data;
    },

    // Task-related methods
    getTasks: async (): Promise<Task[]> => {
        const response = await api.get<Task[]>('/api/v1/private/task');
        return response.data;
    },

    getTasksByTeacher: async (teacherId: number): Promise<Task[]> => {
        const response = await api.get<Task[]>(`/api/v1/private/task/teacher/${teacherId}`);
        return response.data;
    },

    getTasksByStatus: async (status: string): Promise<Task[]> => {
        const response = await api.get<Task[]>(`/api/v1/private/task?status=${status}`);
        return response.data;
    }
};