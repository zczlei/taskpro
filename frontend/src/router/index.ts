import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PersonalTasksView from '../views/PersonalTasksView.vue'
import AccountView from '../views/AccountView.vue'
import LoginView from '../views/LoginView.vue'
import ProjectProgressView from '@/views/ProjectProgressView.vue'
import RequirementManagementView from '@/views/RequirementManagementView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/requirement-management'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        requiresAuth: false
      }
    },
    {
      path: '/home',
      redirect: '/requirement-management'
    },
    {
      path: '/project-requirements',
      name: 'project-requirements',
      component: HomeView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/requirement-management',
      name: 'requirement-management',
      component: RequirementManagementView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/personal-tasks',
      name: 'personal-tasks',
      component: PersonalTasksView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/account',
      name: 'account',
      component: AccountView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/progress',
      name: 'progress',
      component: ProjectProgressView,
      meta: { requiresAuth: true }
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    // 需要登录但未登录，重定向到登录页
    next({ name: 'login' })
  } else if (to.name === 'login' && token) {
    // 已登录但访问登录页，重定向到项目需求页面
    next({ name: 'project-requirements' })
  } else {
    next()
  }
})

export default router
