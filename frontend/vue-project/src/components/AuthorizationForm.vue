<template>
    <el-container class="login-layout" >
        <div class="login-form">
            <el-form style="min-height: 300px;" status-icon ref="formRef" :model="form" :rules="rules" :show-message=false label-width="120px">
                <el-form-item :label="t('auth.login')" prop="login">
                    <el-popover placement="right" :width="200" trigger="hover">
                        <template #reference>
                            <el-input :autofocus=true v-model="form.login" @keyup.enter="auth(formRef)" />
                        </template>
                        <div class="popover">{{ t('validation.login_form.login_incorrect') }}</div>
                    </el-popover>
                </el-form-item>
                <el-form-item :label="t('auth.password')" prop="password">
                    <el-popover placement="right" :width="200" trigger="hover">
                        <template #reference>
                            <el-input v-model="form.password" type="password" autocomplete="off" @keyup.enter="auth(formRef)" />
                        </template>
                        <div class="popover">{{ t('validation.login_form.password_incorrect') }}</div>
                    </el-popover>
                </el-form-item>
                <el-button type="primary" @click="auth(formRef)"> {{ t('button.submit') }} </el-button>
            </el-form>
        </div>
    </el-container>
</template>

<script lang="ts">
import { defineComponent, reactive, ref, SetupContext } from 'vue'
import { useI18n } from 'vue-i18n'
import { FormInstance, FormRules } from 'element-plus'

export default defineComponent({
    name: 'AuthorizationForm',
    emits: ['auth'],
    setup(_: any, context: SetupContext) {
        const { t } = useI18n()
        const formRef = ref<FormInstance>()
        const form = reactive({ login: '', password: '' })
        const loginRegExp = new RegExp('[0-9a-zA-Z\\._]{1,15}');
        const loginValidator = (rule: any, value: any, callback: any) => {
            if (value == null || value == '' || !loginRegExp.test(value)) {
                callback(new Error())
                return
            }
            callback()
        }
        const passwordRegExp = new RegExp('[0-9a-zA-Z\\._]{1,30}');
        const passwordValidator = (rule: any, value: any, callback: any) => {
            if (value == null || value == '' || !passwordRegExp.test(value)) {
                callback(new Error())
                return
            }
            callback()
        }
        const rules = reactive<FormRules>({
            login: [ { validator: loginValidator, trigger: 'change' } ], 
            password: [ { validator: passwordValidator, trigger: 'change' } ]
        })
        const sendAuth = (data: string) => {
            context.emit('auth', data)
        }
        return { t, formRef, form, rules, sendAuth }
    },
    methods: {
        auth(formRef: FormInstance | undefined) {
            if (formRef == undefined) {
                throw new Error('Wrong formRef instance')
            }
            formRef.validate((valid: boolean) => {
                if (valid) {
                    const data = btoa(this.form.login + ':' + this.form.password)
                    this.form.login = '';
                    this.form.password = '';
                    this.sendAuth(data)
                }
                return false
            })
        }
    }
})

</script>

<style>
.login-layout {
    height: 100%;
    width: 100%;
}

.login-form {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
    text-align: center;
}

.popover {
    word-break: break-word;
}
</style>
