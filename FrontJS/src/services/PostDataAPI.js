import axios from 'axios';

export function PostDataAPI(url, data) {

    axios.post(url, data).then(function (response) {
        console.log(response);
        alert("Запрос успешно выполнен!");
    }).catch(function (error) {
        alert("Возникли ошибки", error);
    });

}
