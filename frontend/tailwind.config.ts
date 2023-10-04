import type { Config } from 'tailwindcss'

export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        'border-yellow': '#fef9c2',
        yellow: '#fee400',
        'yellow-300': '#fde047',
        'yellow-400': '#facc15',
        'yellow-500': '#eab308',
        'border-orange': '#e16000',
        orange: '#fa0',
        purple: '#881247',
        darkPurple: '#430027',
        blackPurple: '#1D0011',
        lightGray: '#CCCCCC',
        darkGray: '#727272',
        tutorial: '#FDE39C',
        'tutorial-desktop': 'hsla(86,70%,87%,.7)',
        'green-online': '#3fda36',
        'red-team-bg': '#8f2b1c',
        'red-light': '#e65831'
      },
      screens: {
        '1201px': '1201px',
        '1460px': '1460px'
      },
      borderRadius: {
        '20px': '20px'
      },
      boxShadow: {
        sharp: '4px 4px 0px 0px rgba(0, 0, 0, .59)',
        inset: 'inset 2px 2px 0px 0px silver',
        bottom: '0 4px 0 0 rgba(0,0,0,.3)'
      },
      fontSize: {
        vw: '4.5vw'
      },
      width: {
        '500px': '500px',
        '26vw': '26vw',
        '30vw': '30vw',
        '32vw': '32vw',
        '38vw': '38vw'
      },
      minHeight: {
        '700px': '700px'
      },
      spacing: {
        '50': '50px'
      }
    }
  },
  plugins: []
} satisfies Config
