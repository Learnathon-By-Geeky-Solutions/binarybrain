import api from './api';
import { Course, CreateCourseDto, UpdateCourseDto, CourseFilters } from '../interfaces/course';

export const courseService = {
    // Get all courses (admin only)
    getAllCourses: async (): Promise<Course[]> => {
        const response = await api.get<Course[]>('/api/v1/private/course');
        return response.data;
    },

    // Get courses by author ID
    getCoursesByAuthor: async (authorId: number): Promise<Course[]> => {
        const response = await api.get<Course[]>(`/api/v1/private/course/author/${authorId}`);
        return response.data;
    },

    // Get course by ID
    getCourseById: async (id: number): Promise<Course> => {
        const response = await api.get<Course>(`/api/v1/private/course/${id}`);
        return response.data;
    },

    // Create new course
    createCourse: async (courseData: CreateCourseDto): Promise<Course> => {
        const response = await api.post<Course>('/api/v1/private/course', courseData);
        return response.data;
    },

    // Update course
    updateCourse: async (courseData: UpdateCourseDto): Promise<Course> => {
        const response = await api.put<Course>(`/api/v1/private/course/${courseData.id}`, courseData);
        return response.data;
    },

    // Delete course
    deleteCourse: async (id: number): Promise<void> => {
        await api.delete(`/api/v1/private/course/${id}`);
    },

    // Get courses by IDs
    getCoursesByIds: async (courseIds: number[]): Promise<Course[]> => {
        const response = await api.get<Course[]>('/api/v1/private/course/by-ids', {
            params: { courseIds: courseIds.join(',') }
        });
        return response.data;
    },

    // Add task to course
    addTaskToCourse: async (courseId: number, taskId: number): Promise<Course> => {
        const response = await api.put<Course>(`/api/v1/private/course/${courseId}/add-task/${taskId}`);
        return response.data;
    },

    // Remove task from course
    removeTaskFromCourse: async (courseId: number, taskId: number): Promise<Course> => {
        const response = await api.put<Course>(`/api/v1/private/course/${courseId}/remove-task/${taskId}`);
        return response.data;
    },

    // Get all tasks from a course
    getTasksFromCourse: async (courseId: number): Promise<any[]> => {
        const response = await api.get<any[]>(`/api/v1/private/course/${courseId}/tasks`);
        return response.data;
    }
};