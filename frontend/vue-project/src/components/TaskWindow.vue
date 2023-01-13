<template>
    <el-container v-loading="actionBlock">
        <el-header style="display: flex; align-items: center;">
            <el-row>
                <el-button type="success" v-if="editMode" @click="save(formRef)">{{ t('win.save') }}</el-button>
                <el-button type="warning" v-if="!editMode" @click="edit(true)">{{ t('win.edit') }}</el-button>
                <el-button type="danger" v-if="!editMode" @click="remove(task)">{{ t('win.remove') }}</el-button>
            </el-row>
        </el-header>
        <el-main>
            <el-form status-icon ref="formRef" :model="form" :rules="rules" label-width="120px">
                <el-form-item :label="t('win.title')" prop="title">
                    <el-input v-model="form.title" :disabled="!editMode" />
                </el-form-item>
                <el-form-item :label="t('win.description')" prop="description">
                    <el-input v-model="form.description" :disabled="!editMode" type="textarea" />
                </el-form-item>
                <el-form-item :label="t('win.completed')" prop="completed">
                    <el-switch v-model="form.completed" :disabled="!editMode" />
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>
  
<script lang="ts">
import { ref, Ref, reactive, defineComponent, SetupContext } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElNotification, FormInstance, FormRules } from 'element-plus'
import TaskUI from '@/class/task-ui'
import TaskForm from '@/type/task-form'

export default defineComponent({
    name: 'TaskWindow',
    props: {
        task: {
            required: true,
            type: TaskUI
        },
        csrfToken: {
            required: true,
            type: String
        }
    },
    emits: ['update', 'reset', 'remove', 'notify'],
    setup(_: any, context: SetupContext) {
        const { t } = useI18n()
        const actionBlock = ref(false)
        const isActionBlock = () => {
            return actionBlock.value;
        }
        const setActionBlock = (val: boolean) => {
            actionBlock.value = val;
        }
        const formRef = ref<FormInstance>()
        const editMode: Ref<boolean> = ref(false)
        const taskForm: TaskForm = {
            id: null,
            title: '',
            description: '',
            completed: false
        }
        const form = reactive(taskForm)
        const rules = reactive<FormRules>({
            title: [
                { required: true, message: t('validation.task_form.title_empty'), trigger: 'change' },
                { max: 255, message: t('validation.task_form.title_incorrect'), trigger: 'change' },
            ],
            description: [
                { required: true, message: t('validation.task_form.description_empty'), trigger: 'change' },
                { max: 255, message: t('validation.task_form.description_incorrect'), trigger: 'change' },
            ]
        })
        const copyDataToForm = (task: TaskUI) => {
            form.title = task.title;
            form.description = task.description;
            form.completed = task.completed;
            form.id = task.id;
        }
        const sendUpdate = (task: TaskUI, isNew: boolean, id: string, title: string, description: string, completed: boolean) => {
            context.emit('update', task, isNew, id, title, description, completed)
        }
        const sendReset = (task: TaskUI) => {
            context.emit('reset', task)
        }
        const sendRemove = (task: TaskUI) => {
            context.emit('remove', task)
        }
        const sendNotify = (title: string, message: string) => {
            context.emit('notify', title, message)
        }
        return { t, actionBlock, isActionBlock, setActionBlock, formRef, editMode, form, rules, copyDataToForm, sendUpdate, sendReset, sendRemove, sendNotify }
    },
    mounted() {
        this.select(this.task)
    },
    beforeUpdate() {
        this.select(this.task)
    },
    methods: {
        save(formRef: FormInstance | undefined) {
            if (this.isActionBlock()) {
                return;
            }
            this.setActionBlock(true)
            if (formRef == undefined) {
                throw new Error('Wrong formRef instance')
            }
            formRef.validate((valid) => {
                if (valid) {
                    fetch('api/task', {
                        headers: {
                            'X-CSRF-TOKEN': this.csrfToken,
                            'Accept': 'text/plain',
                            'Content-Type': 'application/json'
                        },
                        method: this.task.isNew ? 'POST' : 'PUT',
                        body: JSON.stringify(this.form)
                    }).then(response => {
                        if (response.ok) {
                            return response.text()
                        }
                        throw new Error('Wrong return status code: ' + response.status)
                    }).then((id: string) => {
                        this.form.id = id;
                        this.sendUpdate(this.task, false, this.form.id, this.form.title, this.form.description, this.form.completed)
                        this.edit(false);
                        this.sendReset(this.task);
                        this.setActionBlock(false)
                    }).catch(e => {
                        console.error(e);
                        this.setActionBlock(false)
                        this.sendNotify('alert.unknown_error.title', 'alert.unknown_error.message')
                    })
                } else {
                    this.setActionBlock(false)
                }
                return false
            })
        },
        edit(editMode: boolean) {
            this.editMode = editMode;
        },
        select(task: TaskUI) {
            if (this.isActionBlock()) {
                return;
            }
            this.setActionBlock(true)
            this.copyDataToForm(task)
            this.edit(task.isNew);
            this.setActionBlock(false)
        },
        remove(task: TaskUI) {
            if (this.isActionBlock()) {
                return;
            }
            this.setActionBlock(true)
            fetch('api/task/' + task.id, {
                headers: {
                    'X-CSRF-TOKEN': this.csrfToken,
                    'Accept': 'text/plain'
                },
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    return response.text()
                }
                throw new Error('Wrong return status code: ' + response.status)
            }).then(() => {
                this.sendReset(task);
                this.sendRemove(task);
                this.setActionBlock(false)
            }).catch(e => {
                console.error(e);
                this.setActionBlock(false)
                ElNotification({
                    title: this.t('alert.unknown_error.title'),
                    message: this.t('alert.unknown_error.message'),
                    duration: 0,
                })
            })
        }
    }
})

</script>

<style>

</style>
