import { NbMenuItem } from '@nebular/theme';

export const MENU_ITEMS: NbMenuItem[] = [
  {
    title: 'HOME',
    group: true,
  },
  {
    title: 'Dashboard',
    icon: 'nb-home',
    link: '/pages/admindashboard',
    home: true,
  },
  {
    title: 'FEATURES',
    group: true,
  },
  {
    title: 'CountDB',
    icon: 'nb-layout-two-column',
    link: '/pages/countdb',
    home: true,
  },
  {
    title: 'TraceDB',
    icon: 'nb-layout-default',
    link: '/pages/tracedb',
    home: true,
  },
  {
    title: 'User',
    icon: 'nb-person',
    link: '/pages/user',
    home: true,
  },
  /*
  {
    title: 'Auth',
    icon: 'nb-locked',
    children: [
      {
        title: 'Login',
        link: '/auth/login',
      },
      {
        title: 'Register',
        link: '/auth/register',
      },
      {
        title: 'Request Password',
        link: '/auth/request-password',
      },
      {
        title: 'Reset Password',
        link: '/auth/reset-password',
      },
    ],
  }, */
];
