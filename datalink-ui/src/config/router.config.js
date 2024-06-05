// eslint-disable-next-line
import { UserLayout, BasicLayout, BlankLayout } from '@/layouts'
import { bxAnaalyse } from '@/core/icons'

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
        path: '/dashboard',
        name: 'dashboard',
        redirect: '/dashboard/monitor',
        component: RouteView,
        meta: { title: 'menu.dashboard', keepAlive: true, icon: bxAnaalyse, permission: ['dashboard'] },
        children: [
          {
            path: '/dashboard/monitor',
            name: 'Monitor',
            component: () => import('@/views/dashboard/Monitor'),
            meta: { title: 'menu.dashboard.monitor', keepAlive: false }
          },
          {
            path: '/dashboard/monitor/data',
            name: 'Data',
            component: () => import('@/views/dashboard/DataMonitor'),
            meta: { title: 'menu.dashboard.dataMonitor', keepAlive: true, permission: ['dashboard'] }
          }
        ]
      },
      {
        path: '/resource',
        name: 'resource',
        component: RouteView,
        meta: { title: 'menu.resource', keepAlive: true, icon: bxAnaalyse, permission: ['resource'] },
        children: [
          {
            path: '/resource/list',
            name: 'resourceList',
            component: () => import('@/views/resource/ResourceList'),
            meta: { title: 'menu.resource.list', keepAlive: true, permission: ['resource'] }
          }
        ]
      },
      {
        path: '/rule',
        name: 'rule',
        component: RouteView,
        meta: { title: 'menu.rule', keepAlive: true, icon: bxAnaalyse, permission: ['rule'] },
        children: [
          {
            path: '/rule/list',
            name: 'ruleList',
            component: () => import('@/views/rule/RuleList'),
            meta: { title: 'menu.rule.list', keepAlive: true, permission: ['rule'] }
          },
          {
            path: '/rule/info',
            name: 'ruleInfo',
            component: () => import('@/views/rule/modules/RuleModel'),
            props: true,
            hidden: true
          }
        ]
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
