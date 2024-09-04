import request from '@/utils/request'

const systemApi = {
    systemInfo: '/api/system/info',

}

export function getSystemInfo() {
    return request({
        url: systemApi.systemInfo,
        method: 'get'
    })
}


