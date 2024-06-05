import request from '@/utils/request'

const systemApi = {
    systemInfo: '/api/system/info',
    // Clients: '/v1/system/clients',
    // Subscribes: '/v1/system/subscribes'
}

export function getSystemInfo() {
    return request({
        url: systemApi.systemInfo,
        method: 'get'
    })
}

// export function getClients(parameter) {
//     return request({
//         url: systemApi.Clients,
//         method: 'get',
//         params: parameter
//     })
// }
//
// export function getSubscribes(parameter) {
//     return request({
//         url: systemApi.Subscribes,
//         method: 'get',
//         params: parameter
//     })
// }
