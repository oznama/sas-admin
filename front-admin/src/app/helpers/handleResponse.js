export const checkResponse = (response) => {
    if(response.code && response.code === 401) {
        onLogout();
    } else if (response.code && response.code !== 200) {
        if( response.message ) {
            setMsgError(response.message)
        } else if (response.errors) {
            setErros(response.errors);
        }
    }
}