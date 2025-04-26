export interface Course {
    id: number;
    title: string;
    description: string;
    authorId: number;
    status: 'OPEN' | 'CLOSED';
    createdAt: string;
    updatedAt: string;
}

export interface Classroom {
    id: number;
    name: string;
    description: string;
    teacherId: number;
    students: number[];
    courses: number[];
    createdAt: string;
    updatedAt: string;
}

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

export interface DashboardData {
    courses: Course[];
    classrooms: Classroom[];
    tasks: Task[];
}