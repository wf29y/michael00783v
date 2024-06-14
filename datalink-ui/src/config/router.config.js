// eslint-disable-next-line
import { UserLayout, BasicLayout, BlankLayout } from '@/layouts'
import { home,resource,rule,script } from '@/core/icons'

const RouteView = {
  name: 'RouteView',
  render: h => h('router-view')
}

export const asyncRouterMap = [
  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: { title: 'menu.home' },
    redirect: '/dashboard/monitor',
    children: [
      {
        path: '/dashboard/monitor',
        name: 'Monitor',
        component: () => import('@/views/dashboard/Monitor'),
        meta: { title: 'menu.dashboard', keepAlive: false, icon: home, permission: ['dashboard'] },
      },
      {
        path: '/resource/list',
        name: 'resourceList',
        component: () => import('@/views/resource/ResourceList'),
        meta: { title: 'menu.resource', keepAlive: true, icon: resource, permission: ['resource'] },
      },
      {
        path: '/rule/list',
        name: 'ruleList',
        component: () => import('@/views/rule/RuleList'),
        meta: { title: 'menu.rule', keepAlive: true, icon: rule, permission: ['rule'] },
      },
      {
        path: '/rule/info',
        name: 'ruleInfo',
        component: () => import('@/views/rule/RuleModel'),
        props: true,
        hidden: true
      },
      {
        path: '/script/list',
        name: 'scriptList',
        component: () => import('@/views/script/ScriptList'),
        meta: { title: 'menu.script', keepAlive: true, icon: script, permission: ['script'] },
      },
      {
        path: '/script/info',
        name: 'scriptInfo',
        component: () => import('@/views/script/modules/ScriptModel'),
        props: true,
        hidden: true
      }
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
