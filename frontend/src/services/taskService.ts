import api from './api';
import {
    Task,
    CreateTaskDto,
    UpdateTaskDto,
    TaskSubmission,
    CreateSubmissionDto,
    GradeSubmissionDto,
} from '../interfaces/task';

export const taskService = {
    // Task-related methods
    getAllTasks: async (): Promise<Task[]> => {
        const response = await api.get<Task[]>('/api/v1/private/task');
        return response.data;
    },

    getTaskById: async (id: number): Promise<Task> => {
        const response = await api.get<Task>(`/api/v1/private/task/${id}`);
        return response.data;
    },

    createTask: async (taskData: CreateTaskDto): Promise<Task> => {
        const response = await api.post<Task>('/api/v1/private/task', taskData);
        return response.data;
    },

    updateTask: async (id: number, taskData: UpdateTaskDto): Promise<Task> => {
        const response = await api.put<Task>(`/api/v1/private/task/${id}`, taskData);
        return response.data;
    },

    deleteTask: async (id: number): Promise<void> => {
        await api.delete(`/api/v1/private/task/${id}`);
    },

    getTasksByTeacher: async (teacherId: number): Promise<Task[]> => {
        const response = await api.get<Task[]>(`/api/v1/private/task/teacher/${teacherId}`);
        return response.data;
    },

    getTasksByStatus: async (status: string): Promise<Task[]> => {
        const response = await api.get<Task[]>(`/api/v1/private/task?status=${status}`);
        return response.data;
    },

    // Task Submission-related methods
    getTaskSubmissions: async (taskId: number): Promise<TaskSubmission[]> => {
        const response = await api.get<TaskSubmission[]>(`/api/v1/private/task/${taskId}/submissions`);
        return response.data;
    },

    getSubmissionById: async (submissionId: number): Promise<TaskSubmission> => {
        const response = await api.get<TaskSubmission>(`/api/v1/private/task/submission/${submissionId}`);
        return response.data;
    },

    createSubmission: async (submissionData: CreateSubmissionDto): Promise<TaskSubmission> => {
        const response = await api.post<TaskSubmission>('/api/v1/private/task/submission', submissionData);
        return response.data;
    },

    gradeSubmission: async (submissionId: number, gradeData: GradeSubmissionDto): Promise<TaskSubmission> => {
        const response = await api.put<TaskSubmission>(
            `/api/v1/private/task/submission/${submissionId}/grade`,
            gradeData
        );
        return response.data;
    },

    getStudentSubmissions: async (studentId: number): Promise<TaskSubmission[]> => {
        const response = await api.get<TaskSubmission[]>(`/api/v1/private/task/submissions/student/${studentId}`);
        return response.data;
    },

    // Course-specific task methods
    getTasksByCourse: async (courseId: number): Promise<Task[]> => {
        const response = await api.get<Task[]>(`/api/v1/private/task/course/${courseId}`);
        return response.data;
    }
};