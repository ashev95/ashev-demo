type WebUser = {
    login: string | undefined;
    roles: Array<string> | undefined;
    isAuthenticated: boolean | undefined;
};

export default WebUser