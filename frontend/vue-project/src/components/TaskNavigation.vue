<template>
    <el-scrollbar>
        <el-menu class="task-nav-menu" :default-active="selectedFrontId" :default-openeds="['1']">
            <el-sub-menu index="1" v-if="dataIsReady">
                <template #title>
                    <el-icon>
                        <List />
                    </el-icon> {{ t('nav.title') }}
                </template>
                <el-menu-item v-for="task in tasks" :key="'1-' + task.frontId" :index="'1-' + task.frontId"
                    v-on:click="sendOpen(task)">
                    <template #title>{{ task.title }}</template>
                </el-menu-item>
            </el-sub-menu>
        </el-menu>
    </el-scrollbar>
</template>

<script lang="ts">

import TaskUI from '@/class/task-ui';
import { defineComponent, ref, PropType, SetupContext } from 'vue'
import { useI18n } from 'vue-i18n'

export default defineComponent({
    name: 'TaskNavigation',
    props: {
        tasks: Array as PropType<Array<TaskUI>>,
        dataIsReady: Boolean
    },
    emits: ['open'],
    setup(_: any, context: SetupContext) {
        const { t } = useI18n()
        const selectedFrontId = ref('1');
        const sendOpen = (task: TaskUI) => {
            context.emit('open', task)
        }
        const active = (task: TaskUI) => {
            selectedFrontId.value = '1-' + task.frontId;
        }
        context.expose({ sendOpen, active })
        return { t, sendOpen, active, selectedFrontId }
    }
})

</script>

<style>
.task-nav-menu {
    height: 100%;
}
</style>
