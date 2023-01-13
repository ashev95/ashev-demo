class TaskUI {
    id: string | null;
    title: string;
    description: string;
    completed: boolean;
    frontId: number;
    isNew: boolean;

    constructor(id: string | null,
        title: string,
        description: string,
        completed: boolean,
        frontId: number,
        isNew: boolean) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.frontId = frontId;
        this.isNew = isNew;
    }

}

export default TaskUI