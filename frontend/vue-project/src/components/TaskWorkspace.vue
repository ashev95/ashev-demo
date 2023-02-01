<template>
    <el-container class="app-layout" >
        <el-header
            style="width: 100%; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--el-border-color);">
            <div style="display: inline-flex;">
                <el-button :disabled="newFrontId > 0" @click="add()"> {{ t('button.add') }} </el-button>
            </div>
            <div style="display: inline-flex;">
                <el-icon style="margin: 5px;">
                    <User />
                </el-icon>
                <text style="margin: 5px;">{{ profile.login }}</text>
                <el-button @click="sendLogout()"> {{ t('button.logout') }} </el-button>
            </div>
        </el-header>
        <el-container>
            <el-aside width="200px" class="app-aside">
                <el-scrollbar class="app-scrollbar">
                    <TaskNavigation ref="taskNavigation" @open="open" :tasks="tasks" :dataIsReady="dataIsReady" />
                </el-scrollbar>
            </el-aside>
            <el-main class="app-main">
                <TaskWindow ref="taskWindow" @Update="update" @reset="reset" @remove="remove"
                    v-if="selectedItem.frontId > 0" :csrf-token="csrfToken" :task="selectedItem" />
            </el-main>
        </el-container>
    </el-container>
</template>

<script lang="ts">

import { defineComponent, ref, SetupContext } from 'vue'
import { useI18n } from 'vue-i18n'

import TaskNavigation from './TaskNavigation.vue'
import TaskWindow from './TaskWindow.vue'

import Task from '@/type/task'
import TaskUI from '@/class/task-ui'
import WebUser from '@/type/web-user'

export default defineComponent({
    name: 'TaskWorkspace',
    components: {
        TaskNavigation,
        TaskWindow
    },
    props: {
        csrfToken: {
            required: true,
            type: String
        }
    },
    emits: ['notify', 'logout', 'stop-loading'],
    setup(_: any, context: SetupContext) {
        const { t } = useI18n()
        const frontIdCounter = ref(0)
        const selectedItem = ref(new TaskUI(null, '', '', false, 0, false))
        const tasks = ref<TaskUI[]>(new Array(undefined) as Array<TaskUI>)
        const taskWindow = ref<InstanceType<typeof TaskWindow> | null>(null)
        const open = (task: TaskUI) => {
            selectedItem.value = task;
        }
        const update = (task: TaskUI, isNew: boolean, id: string, title: string, description: string, completed: boolean) => {
            task.isNew = isNew
            task.id = id;
            task.title = title;
            task.description = description;
            task.completed = completed;
        }
        const remove = (task: TaskUI) => {
            const i = tasks.value.findIndex(task1 => task1.frontId == task.frontId);
            if (i > -1) {
                tasks.value.splice(i, 1);
                selectedItem.value = new TaskUI(null, '', '', false, 0, false);
            }
        }
        const taskNavigation = ref<InstanceType<typeof TaskNavigation> | null>(null)
        const openFromNav = (task: TaskUI) => {
            taskNavigation.value?.sendOpen(task)
            taskNavigation.value?.active(task)
        }
        const sendNotify = (title: string, message: string) => {
            context.emit('notify', title, message)
        }
        const sendLogout = () => {
            context.emit('logout')
        }
        const getStartFrontId = () => {
            return 0
        }
        const getEmptyUser = () => {
            const webUser: WebUser = { login: undefined, roles: undefined, isAuthenticated: false }
            return webUser
        }
        const newFrontId = ref(getStartFrontId())
        const profile = ref(getEmptyUser())
        const clear = () => {
            newFrontId.value = getStartFrontId()
            profile.value = getEmptyUser()
        }
        const dataIsReady = ref(false)
        const sendStopLoading = () => {
            context.emit('stop-loading')
        }
        context.expose({ clear })
        return {
            t,
            frontIdCounter,
            selectedItem,
            tasks,
            taskWindow,
            open,
            update,
            remove,
            taskNavigation,
            openFromNav,
            sendNotify,
            sendLogout,
            newFrontId,
            profile,
            clear,
            dataIsReady,
            sendStopLoading
        }
    },
    beforeCreate() {
        fetch('api/client/profile')
        .then(response => response.json())
        .then((data: WebUser) => {
            console.log(data);
            this.profile = data;
            return fetch('api/task')
        }).then(response => {
            if (response.ok) {
                if (response.status === 204) {
                    return []
                } else {
                    return response.json()
                }
            }
            throw new Error('Wrong return status code: ' + response.status)
        }).then((tasks: Task[]) => {
            console.log(tasks);
            const tasksUI = new Array<TaskUI>();
            for (let task of tasks) {
                const taskUI = new TaskUI(task.id, task.title, task.description, task.completed, ++this.frontIdCounter, false)
                tasksUI.push(taskUI)
            }
            this.tasks = tasksUI;
            this.dataIsReady = true
            this.sendStopLoading()
        }).catch(e => {
            if (e.message == 'NoError') {
                return;
            }
            console.error(e);
            this.sendNotify('alert.unknown_error.title', 'alert.unknown_error.message')
        })
    },
    methods: {
        add() {
            const taskUI = new TaskUI(null, this.t('tab.new'), '', false, ++this.frontIdCounter, true)
            console.log(taskUI)
            this.tasks.push(taskUI)
            this.newFrontId = taskUI.frontId;
            this.openFromNav(taskUI)
        },
        reset(task: TaskUI) {
            if (task.frontId != this.newFrontId) {
                return;
            }
            this.newFrontId = 0;
        }
    }
})

</script>

<style>

@media (max-width: 550px) {
    .app-aside {
        height: 30% !important;
        --el-aside-width: 100% !important;
        border-bottom: 1px solid var(--el-border-color) !important;
    }
    .el-container {
        flex-direction: column !important;
    }
}

.app-layout {
    height: 100%;
}

.app-aside {
    height: 100%;
}

.app-scrollbar {
    height: 100%;
}

.app-main {
    height: 100%;
    padding: 0px;
}
</style>
