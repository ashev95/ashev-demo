<template>
    <el-container style="width: 100%; height: 100%">
        <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; width: 100%; text-align: center;">
            <el-tabs v-model="activeName" :stretch="true">
                <el-tab-pane :label="t('auth_tabs.auth')" name="auth">
                    <AuthorizationForm @auth="sendAuth" />
                </el-tab-pane>
                <el-tab-pane :label="t('auth_tabs.register')" name="register">
                    <RegistrationForm @register="sendRegister" />
                </el-tab-pane>
            </el-tabs>
        </div>
    </el-container>
</template>

<script lang="ts">

import { defineComponent, SetupContext } from 'vue'
import { useI18n } from 'vue-i18n'

import AuthorizationForm from './AuthorizationForm.vue'
import RegistrationForm from './RegistrationForm.vue'

export default defineComponent({
    name: "AppAuthorization",
    components: {
        AuthorizationForm,
        RegistrationForm
    },
    emits: ['auth', 'register'],
    setup(_: any, context: SetupContext) {
        const { t } = useI18n();
        const sendAuth = (data: string) => {
            context.emit('auth', data)
        }
        const sendRegister = (username: string, password: string) => {
            context.emit('register', username, password)
        }
        return { t, sendAuth, sendRegister };
    },
    data() {
        return {
            activeName: 'auth'
        }
    }
})

</script>

<style>

</style>
