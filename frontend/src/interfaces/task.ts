export interface Task {
    id: number;
    title: string;
    description: string;
    dueDate: string;
    status: 'OPEN' | 'CLOSED' | 'DONE';
    courseId: number;
    teacherId: number;
    createdAt: string;
    updatedAt: string;
}

export interface CreateTaskDto {
    title: string;
    description: string;
    dueDate: string;
    courseId: number;
}

export interface UpdateTaskDto {
    id: number;
    title: string;
    description: string;
    dueDate: string;
    status: 'OPEN' | 'CLOSED' | 'DONE';
}

export interface TaskSubmission {
    id: number;
    taskId: number;
    studentId: number;
    content: string;
    status: 'SUBMITTED' | 'GRADED';
    grade?: number;
    feedback?: string;
    submittedAt: string;
    gradedAt?: string;
}

export interface CreateSubmissionDto {
    taskId: number;
    content: string;
}

export interface GradeSubmissionDto {
    submissionId: number;
    grade: number;
    feedback: string;
}