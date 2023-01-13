<template>
    <el-container class="register-layout" >
        <div class="register-form">
            <el-form style="min-height: 300px;" status-icon ref="formRef" :model="form" :rules="rules" :show-message=false label-width="120px" >
                <el-form-item :label="t('register.login')" prop="login">
                    <el-popover placement="right" :width="200" trigger="hover">
                        <template #reference>
                            <el-input :autofocus=true v-model="form.login" @keyup.enter="register(formRef)" />
                        </template>
                        <div class="popover">{{ t(loginInfo) }}</div>
                    </el-popover>
                </el-form-item>
                <el-form-item :label="t('register.password')" prop="password">
                    <el-popover placement="right" :width="200" trigger="hover">
                        <template #reference>
                            <el-input v-model="form.password" type="password" autocomplete="off" @keyup.enter="register(formRef)" />
                        </template>
                        <div class="popover">{{ t(passwordInfo) }}</div>
                    </el-popover>
                </el-form-item>
                <el-form-item :label="t('register.password2')" prop="password2">
                    <el-popover placement="right" :width="200" trigger="hover">
                        <template #reference>
                            <el-input v-model="form.password2" type="password" autocomplete="off" @keyup.enter="register(formRef)" />
                        </template>
                        <div class="popover">{{ t(password2Info) }}</div>
                    </el-popover>
                </el-form-item>
                <el-button type="primary" @click="register(formRef)"> {{ t('button.register') }} </el-button>
            </el-form>
        </div>
    </el-container>
</template>

<script lang="ts">

import { defineComponent, reactive, ref, SetupContext } from 'vue'
import { useI18n } from 'vue-i18n'
import { FormInstance, FormRules } from 'element-plus'

export default defineComponent({
    name: 'RegistrationForm',
    setup(_: any, context: SetupContext) {
        const { t } = useI18n()
        const loginInfo = ref('validation.register_form.login_incorrect')
        const passwordInfo = ref('validation.register_form.password_incorrect')
        const password2Info = ref('validation.register_form.password_incorrect')
        const formRef = ref<FormInstance>()
        const form = reactive({ login: '', password: '', password2: '' })
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
                passwordInfo.value = 'validation.register_form.password_incorrect'
                callback(new Error())
                return
            }
            if (form.password2 != null && form.password2 != '' && passwordRegExp.test(form.password2) && form.password2 !== value) {
                passwordInfo.value = 'validation.register_form.passwords_not_equal'
                callback(new Error())
                return
            }
            passwordInfo.value = 'validation.register_form.password_incorrect'
            callback()
        }
        const passwordValidator2 = (rule: any, value: any, callback: any) => {
            if (value == null || value == '' || !passwordRegExp.test(value)) {
                password2Info.value = 'validation.register_form.password_incorrect'
                callback(new Error())
                return
            }
            if (form.password != null && form.password != '' && passwordRegExp.test(form.password) && form.password !== value) {
                password2Info.value = 'validation.register_form.passwords_not_equal'
                callback(new Error())
                return
            }
            password2Info.value = 'validation.register_form.password_incorrect'
            callback()
        }
        const rules = reactive<FormRules>({
            login: [ { validator: loginValidator, trigger: 'change' } ], 
            password: [ { validator: passwordValidator, trigger: 'change' } ],
            password2: [ { validator: passwordValidator2, trigger: 'change' } ]
        })
        const sendRegister = (username: string, password: string) => {
            context.emit('register', username, password)
        }
        return { t, loginInfo, passwordInfo, password2Info, formRef, form, rules, sendRegister }
    },
    methods: {
        register(formRef: FormInstance | undefined) {
            if (formRef == undefined) {
                throw new Error('Wrong formRef instance')
            }
            formRef.validate((valid: boolean) => {
                if (valid) {
                    const login = this.form.login;
                    const password = this.form.password;
                    this.form.login = '';
                    this.form.password = '';
                    this.form.password2 = '';
                    formRef.resetFields()
                    this.sendRegister(login, password)
                }
                return false
            })
        }
    }
})

</script>

<style>
.register-layout {
    height: 100%;
    width: 100%;
}

.register-form {
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
