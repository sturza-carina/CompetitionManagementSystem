export const PROBE_BASE_URL = 'http://localhost:8080/probe';
export const AUTH_BASE_URL = 'http://localhost:8080/auth';

function status(response) {
    console.log('response status ' + response.status);
    if (response.status >= 200 && response.status < 300) {
        return Promise.resolve(response);
    } else {
        return Promise.reject(new Error(response.statusText));
    }
}

function json(response) {
    return response.json();
}

// AUTH

export function Login(username, password) {
    console.log('Inainte de fetch POST login pentru ' + username);

    let myHeaders = new Headers();
    myHeaders.append('Content-Type', 'application/json');

    let antet = {
        method: 'POST',
        headers: myHeaders,
        mode: 'cors',
        body: JSON.stringify({ username, password })
    };

    return fetch(AUTH_BASE_URL + '/login', antet)
        .then(status)
        .then(response => response.text())
        .then(token => {
            console.log('Login reusit, token primit');
            localStorage.setItem('jwt', token);
            return token;
        }).catch(error => {
            console.log('Login failed', error);
            return Promise.reject(error);
        });
}

export function Logout() {
    localStorage.removeItem('jwt');
}

// Helper: adauga Authorization header daca exista token
function addAuthHeader(headers) {
    const token = localStorage.getItem('jwt');
    if (token) {
        headers.append('Authorization', 'Bearer ' + token);
    }
    return headers;
}

// PROBE

export function GetAllProbe() {
    let headers = new Headers();
    headers.append('Accept', 'application/json');

    let myInit = {
        method: 'GET',
        headers: headers,
        mode: 'cors'
    };
    let request = new Request(PROBE_BASE_URL, myInit);

    console.log('Inainte de fetch GET pentru ' + PROBE_BASE_URL);

    return fetch(request)
        .then(status)
        .then(json)
        .then(data => {
            console.log('Request succeeded with JSON response', data);
            return data;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function GetProbeByCategorie(categorie) {
    let headers = new Headers();
    headers.append('Accept', 'application/json');

    let myInit = {
        method: 'GET',
        headers: headers,
        mode: 'cors'
    };
    const url = PROBE_BASE_URL + '?categorie=' + encodeURIComponent(categorie);
    let request = new Request(url, myInit);

    console.log('Inainte de fetch GET filtrare dupa categorie: ' + url);

    return fetch(request)
        .then(status)
        .then(json)
        .then(data => {
            console.log('Filtrare succeeded with JSON response', data);
            return data;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function AddProba(proba) {
    console.log('Inainte de fetch POST ' + JSON.stringify(proba));

    let myHeaders = new Headers();
    myHeaders.append('Accept', 'application/json');
    myHeaders.append('Content-Type', 'application/json');
    addAuthHeader(myHeaders);  // <-- token

    let antet = {
        method: 'POST',
        headers: myHeaders,
        mode: 'cors',
        body: JSON.stringify(proba)
    };

    return fetch(PROBE_BASE_URL, antet)
        .then(status)
        .then(json)
        .then(data => {
            console.log('Proba adaugata cu succes', data);
            return data;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function UpdateProba(id, proba) {
    console.log('Inainte de fetch PUT pentru id=' + id + ' ' + JSON.stringify(proba));

    let myHeaders = new Headers();
    myHeaders.append('Accept', 'application/json');
    myHeaders.append('Content-Type', 'application/json');
    addAuthHeader(myHeaders);  // <-- token

    let antet = {
        method: 'PUT',
        headers: myHeaders,
        mode: 'cors',
        body: JSON.stringify(proba)
    };

    const url = PROBE_BASE_URL + '/' + id;
    console.log('URL pentru update ' + url);

    return fetch(url, antet)
        .then(status)
        .then(json)
        .then(data => {
            console.log('Proba modificata cu succes', data);
            return data;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function DeleteProba(id) {
    console.log('Inainte de fetch DELETE pentru id=' + id);

    let myHeaders = new Headers();
    myHeaders.append('Accept', 'application/json');
    addAuthHeader(myHeaders);  // <-- token

    let antet = {
        method: 'DELETE',
        headers: myHeaders,
        mode: 'cors'
    };

    const url = PROBE_BASE_URL + '/' + id;
    console.log('URL pentru delete ' + url);

    return fetch(url, antet)
        .then(status)
        .then(response => {
            console.log('Delete status ' + response.status);
            return response.text();
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}