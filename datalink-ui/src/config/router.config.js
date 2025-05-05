// eslint-disable-next-line
import { UserLayout, BasicLayout, BlankLayout } from '@/layouts'
import { home,resource,rule,script,variable } from '@/core/icons'

const RouteView = {
  name: 'RouteView',
  render: h => h('router-view')
}

export const asyncRouterMap = [
  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: { title: '监控' },
    redirect: '/dashboard/monitor',
    children: [
      {
        path: '/dashboard/monitor',
        name: 'Monitor',
        component: () => import('@/views/dashboard/Monitor'),
        meta: { title: '监控', keepAlive: false, icon: home, permission: ['dashboard'] },
      },
      {
        path: '/resource/list',
        name: 'resourceList',
        component: () => import('@/views/resource/ResourceList'),
        meta: { title: '资源', keepAlive: true, icon: resource, permission: ['resource'] },
      },
      {
        path: '/rule/list',
        name: 'ruleList',
        component: () => import('@/views/rule/RuleList'),
        meta: { title: '规则', keepAlive: true, icon: rule, permission: ['rule'] },
      },
      {
        path: '/rule/info/:ruleId',
        name: 'ruleInfo',
        component: () => import('@/views/rule/RuleModel'),
        props: true,
        hidden: true
      },
      {
        path: '/rule/runtime/:ruleId',
        name: 'ruleRuntime',
        component: () => import('@/views/rule/RuleRuntime'),
        props: true,
        hidden: true
      },
      {
        path: '/script/list',
        name: 'scriptList',
        component: () => import('@/views/script/ScriptList'),
        meta: { title: '脚本', keepAlive: true, icon: script, permission: ['script'] },
      },
      {
        path: '/script/info/:scriptId',
        name: 'scriptInfo',
        component: () => import('@/views/script/modules/ScriptModel'),
        props: true,
        hidden: true
      },
      {
        path: '/variable/list',
        name: 'variableList',
        component: () => import('@/views/variable/VariableList'),
        meta: { title: '变量', keepAlive: true, icon: variable, permission: ['variable'] },
      },
    ]
  },
  {
    path: '*',
    redirect: '/404',
    hidden: true
  }
]

/**
 * 基础路由
 * @type { *[] }
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login')
      },
    /*  {
        path: 'register',
        name: 'register',
        component: () => import(/!* webpackChunkName: "user" *!/ '@/views/user/Register')
      },*/
     /* {
        path: 'register-result',
        name: 'registerResult',
        component: () => import(/!* webpackChunkName: "user" *!/ '@/views/user/RegisterResult')
      },*/
      {
        path: 'recover',
        name: 'recover',
        component: undefined
      }
    ]
  },

  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  }
]
