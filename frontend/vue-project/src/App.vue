<template>
    <div v-if="state === PageState.AUTHENTICATED" style="height: 100%">
        <TaskWorkspace ref="taskWorkspace" 
            @stop-loading="stopLoading" 
            @logout="logout" @notify="notify" 
            :csrf-token="csrfToken"
        />
    </div>
    <div v-if="state === PageState.NOT_AUTHENTICATED" style="height: 100%">
        <AppAuthorization @auth="auth" @register="register" />
    </div>
</template>

<script lang="ts">

import { ref, defineComponent } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElNotification, ElLoading } from 'element-plus'

import TaskWorkspace from './components/TaskWorkspace.vue'
import AppAuthorization from './components/AppAuthorization.vue'

import PageState from '@/enum/page-state'

export default defineComponent({
    name: 'App',
    components: {
        TaskWorkspace,
        AppAuthorization
    },
    setup() {
        const { t } = useI18n()
        const notify = (title: string, message: string) => {
            ElNotification({
                title: t(title),
                message: t(message),
                duration: 0
            })
        }
        const startAndGetLoading = () => {
            const loading = ElLoading.service({
                lock: true,
                fullscreen: true,
                text: t('loading.title')
            })
            return loading;
        }
        const startLoading = () => {
            startAndGetLoading()
        }
        const stopLoading = () => {
            startAndGetLoading().close()
        }
        const csrfToken = ref('')
        const setToken = (token: string) => {
            csrfToken.value = token;
        }
        const state = ref(PageState.LOADING)
        const changeState = (newState: PageState) => {
            state.value = newState
        }
        startLoading();
        const taskWorkspace = ref<InstanceType<typeof TaskWorkspace> | null>(null)
        const clearTaskWorkspace = () => {
            taskWorkspace.value?.clear()
        }
        const auth = (data: string) => {
            changeState(PageState.LOADING)
            startLoading();
            fetch('/api', {
                headers: {
                    'Authorization': 'Basic ' + data
                }
            }).then(response => {
                if (response.ok) {
                    return response.text();
                } else if (response.status === 401) {
                    console.error(response);
                    notify('alert.login_error.title', 'alert.login_error.message')
                    changeState(PageState.NOT_AUTHENTICATED)
                    stopLoading()
                    return;
                }
                throw new Error('Wrong return status code: ' + response.status)
            }).then((htmlPage: string | undefined) => {
                if (htmlPage == undefined) {
                    return;
                }
                const htmlDOM = new DOMParser().parseFromString(htmlPage, "text/html");
                const htmlElement: HTMLInputElement | null = htmlDOM.querySelector('form > input[name="_csrf"]')
                if (htmlElement == null) {
                    throw new Error('Wrong response content')
                }
                setToken(htmlElement.value)
                changeState(PageState.AUTHENTICATED)
                //stopLoading() <-- call in child component
            }).catch(e => {
                console.error(e);
                notify('alert.unknown_error.title', 'alert.unknown_error.message')
            });
        }
        const register = (username: string, password: string) => {
            startLoading();
            fetch('/api/user/new', {
                headers: {
                    'Content-Type': 'application/json'
                },
                method: 'post',
                body: JSON.stringify({
                    login: username,
                    password: password
                })
            }).then(response => {
                if (response.ok) {
                    notify('alert.register_success.title', 'alert.register_success.message')
                } else if (response.status === 400) {
                    console.error(response);
                    notify('alert.user_already_exists.title', 'alert.user_already_exists.message')
                } else {
                    throw new Error('Wrong return status code: ' + response.status)
                }
                stopLoading()
            }).catch(e => {
                console.error(e);
                notify('alert.unknown_error.title', 'alert.unknown_error.message')
            });
        }
        return {
            t,
            notify,
            startLoading,
            stopLoading,
            csrfToken,
            setToken,
            state,
            changeState,
            taskWorkspace,
            clearTaskWorkspace,
            PageState,
            auth,
            register
        }
    },
    data() {
        return {}
    },
    mounted() {
        document.title = this.t('app.name');
        fetch('/api')
        .then(response => {
            if (response.ok) {
                return response.text()
            } else if (response.status === 401) {
                this.changeState(PageState.NOT_AUTHENTICATED)
                this.stopLoading()
                throw new Error('NoError')
            }
            throw new Error('Wrong return status code: ' + response.status)
        }).then((htmlPage: string | undefined) => {
            if (htmlPage == undefined) {
                throw new Error('Response content is empty')
            }
            const htmlDOM = new DOMParser().parseFromString(htmlPage, "text/html");
            const htmlElement: HTMLInputElement | null = htmlDOM.querySelector('form > input[name="_csrf"]')
            if (htmlElement == null) {
                throw new Error('Wrong response content')
            }
            this.csrfToken = htmlElement.value;
            this.changeState(PageState.AUTHENTICATED)
            //this.stopLoading() <-- call in child component
        }).catch(e => {
            if (e.message == 'NoError') {
                return;
            }
            console.error(e);
            this.notify('alert.unknown_error.title', 'alert.unknown_error.message')
        })
    },
    methods: {
        logout() {
            this.changeState(PageState.LOADING)
            this.startLoading();
            fetch('api')
                .then(response => response.text())
                .then((data: string) => {
                    const html = new DOMParser().parseFromString(data, 'text/xml');
                    const form: HTMLFormElement | null = html.querySelector('form');
                    if (form == null) {
                        throw new Error('Wrong response content')
                    }
                    const formData = new FormData(form);
                    return fetch('api/logout', {
                        method: 'POST',
                        body: formData
                    })
                }).then(response => {
                    if (response.status == 204) {
                        this.clearTaskWorkspace()
                        this.changeState(PageState.NOT_AUTHENTICATED)
                        this.stopLoading()
                    }
                }).catch(e => {
                    console.error(e);
                    this.notify('alert.unknown_error.title', 'alert.unknown_error.message')
                })
        }
    }
})
</script>

<style>
#app {
    height: 100%;
}

body {
    margin: 0px;
    padding: 0px;
    width: 100vw;
    height: 100vh;
    border: 0px;
}
</style>
