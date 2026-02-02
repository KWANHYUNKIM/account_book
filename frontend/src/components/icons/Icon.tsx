'use client'

import { getIconName } from './IconMapper'

interface IconProps {
  name: string
  size?: number
  color?: string
  className?: string
}

export default function Icon({ name, size = 24, color = 'currentColor', className }: IconProps) {
  // 이모지나 아이콘 이름을 정규화
  const normalizedName = getIconName(name)
  
  const icons: Record<string, JSX.Element> = {
    // 네비게이션 아이콘
    dashboard: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M3 13H11V3H3V13ZM3 21H11V15H3V21ZM13 21H21V11H13V21ZM13 3V9H21V3H13Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    folder: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M10 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H20C20.5304 22 21.0391 21.7893 21.4142 21.4142C21.7893 21.0391 22 20.5304 22 20V8C22 7.46957 21.7893 6.96086 21.4142 6.58579C21.0391 6.21071 20.5304 6 20 6H12L10 4Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    bank: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 2L2 7L12 12L22 7L12 2Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
        <path d="M2 17L12 22L22 17V9L12 14L2 9V17Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    settings: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 15.5C13.933 15.5 15.5 13.933 15.5 12C15.5 10.067 13.933 8.5 12 8.5C10.067 8.5 8.5 10.067 8.5 12C8.5 13.933 10.067 15.5 12 15.5Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
        <path d="M19.43 12.97C19.47 12.66 19.5 12.33 19.5 12C19.5 11.67 19.47 11.34 19.43 11.03L21.54 9.37C21.73 9.22 21.78 8.95 21.66 8.73L19.66 5.27C19.54 5.05 19.27 4.96 19.05 5.05L16.56 6.05C16.04 5.65 15.5 5.32 14.87 5.07L14.49 2.42C14.46 2.18 14.25 2 14 2H10C9.75 2 9.54 2.18 9.51 2.42L9.13 5.07C8.5 5.32 7.96 5.66 7.44 6.05L4.95 5.05C4.73 4.96 4.46 5.05 4.34 5.27L2.34 8.73C2.22 8.95 2.27 9.22 2.46 9.37L4.57 11.03C4.53 11.34 4.5 11.67 4.5 12C4.5 12.33 4.53 12.66 4.57 12.97L2.46 14.63C2.27 14.78 2.22 15.05 2.34 15.27L4.34 18.73C4.46 18.95 4.73 19.04 4.95 18.95L7.44 17.95C7.96 18.35 8.5 18.68 9.13 18.93L9.51 21.58C9.54 21.82 9.75 22 10 22H14C14.25 22 14.46 21.82 14.49 21.58L14.87 18.93C15.5 18.68 16.04 18.34 16.56 17.95L19.05 18.95C19.27 19.04 19.54 18.95 19.66 18.73L21.66 15.27C21.78 15.05 21.73 14.78 21.54 14.63L19.43 12.97Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    logout: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M17 7L15.59 8.41L18.17 11H8V13H18.17L15.59 15.59L17 17L22 12L17 7Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
        <path d="M4 5H12V3H4C2.9 3 2 3.9 2 5V19C2 20.1 2.9 21 4 21H12V19H4V5Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    // 세션 아이콘들
    money: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM13.41 19.09V16.09C14.44 15.84 15.16 15.06 15.44 14.09H17.09C16.74 15.66 15.78 16.96 14.5 17.7L13.41 19.09ZM14.5 6.3L15.59 4.91C16.96 5.66 17.94 6.96 18.3 8.55H16.56C16.28 7.57 15.55 6.79 14.5 6.3ZM4.91 13.41H6.56C6.84 14.38 7.56 15.16 8.59 15.41V18.41C7.05 18.16 5.66 17.3 4.91 16.09L4.91 13.41ZM4.91 10.59C5.66 9.28 7.05 8.42 8.59 8.17V11.17C7.56 11.42 6.84 12.2 6.56 13.17H4.91V10.59ZM10.59 4.91H13.41V6.56C12.44 6.84 11.66 7.56 11.41 8.59H8.41C8.66 7.05 9.52 5.66 10.59 4.91ZM15.41 19.09L14.5 17.7C13.22 16.96 12.26 15.66 11.91 14.09H13.56C13.84 15.06 14.56 15.84 15.41 16.09V19.09ZM10.59 19.09V16.09C9.56 15.84 8.84 15.06 8.56 14.09H6.91C7.26 15.66 8.22 16.96 9.5 17.7L10.59 19.09ZM11.41 15.41C11.66 14.38 12.44 13.66 13.41 13.41V10.41C11.87 10.66 10.48 11.52 9.73 12.59H11.41V15.41ZM12 8C13.1 8 14 8.9 14 10C14 11.1 13.1 12 12 12C10.9 12 10 11.1 10 10C10 8.9 10.9 8 12 8ZM15.44 9.45H17.09C16.74 7.88 15.78 6.58 14.5 5.84L13.41 4.45V7.45C14.44 7.7 15.16 8.48 15.44 9.45Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    chart: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M3 13H11V3H3V13ZM3 21H11V15H3V21ZM13 21H21V11H13V21ZM13 3V9H21V3H13Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    card: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M20 4H4C2.89 4 2.01 4.89 2.01 6L2 18C2 19.11 2.89 20 4 20H20C21.11 20 22 19.11 22 18V6C22 4.89 21.11 4 20 4ZM20 18H4V12H20V18ZM20 8H4V6H20V8Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    trend: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M16 6L18.29 8.29L13.41 13.17L9.41 9.17L2 16.59L3.41 18L9.41 12L13.41 16L19.71 9.71L22 12V6H16Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    cash: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M11.8 10.9C9.53 10.31 8.8 9.7 8.8 8.75C8.8 7.66 9.81 6.9 11.5 6.9C13.28 6.9 13.94 7.75 14 9H16.21C16.14 7.28 15.09 5.7 13 5.19V3H10V5.16C8.06 5.58 6.5 6.84 6.5 8.77C6.5 11.08 8.41 12.23 11.2 12.9C13.7 13.5 14.2 14.38 14.2 15.31C14.2 16 13.71 17.1 11.5 17.1C9.44 17.1 8.63 16.18 8.5 15H6.32C6.44 17.19 8.08 18.42 10 18.83V21H13V18.85C14.95 18.5 16.5 17.35 16.5 15.3C16.5 12.46 14.07 11.5 11.8 10.9Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    target: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM13 17H11V15H13V17ZM13 13H11V7H13V13Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    phone: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M6.62 10.79C8.06 13.62 10.38 15.94 13.21 17.38L15.41 15.18C15.69 14.9 16.08 14.82 16.43 14.93C17.55 15.3 18.75 15.5 20 15.5C20.55 15.5 21 15.95 21 16.5V20C21 20.55 20.55 21 20 21C10.61 21 3 13.39 3 4C3 3.45 3.45 3 4 3H7.5C8.05 3 8.5 3.45 8.5 4C8.5 5.25 8.7 6.45 9.07 7.57C9.18 7.92 9.1 8.31 8.82 8.59L6.62 10.79Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    plane: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M21 16V14L15 11V5C15 3.9 14.1 3 13 3H11C9.9 3 9 3.9 9 5V11L3 14V16L9 13.5V19L7 21V23L12 22L17 23V21L15 19V13.5L21 16Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    home: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M10 20V14H14V20H19V12H22L12 3L2 12H5V20H10Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    food: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M8.1 13.34L11 16.24L18.24 9L15.34 6.1L8.1 13.34ZM22.61 2.22L21.78 1.39C21.39 1 20.78 1 20.39 1.39L19.61 2.17L21.83 4.39L22.61 2.22ZM2 22L15.5 8.5L13.5 6.5L0 20V22H2Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
    movie: (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M18 4L20 8H17L15 4H18ZM18 20L20 16H17L15 20H18ZM8 4L10 8H7L5 4H8ZM8 20L10 16H7L5 20H8ZM4 4L6 8H3L1 4H4ZM4 20L6 16H3L1 20H4ZM12 4L14 8H11L9 4H12ZM12 20L14 16H11L9 20H12ZM20 10L22 14H19L17 10H20ZM4 10L6 14H3L1 10H4ZM12 10L14 14H11L9 10H12Z" fill={color} fillRule="evenodd" clipRule="evenodd"/>
      </svg>
    ),
  }

  const icon = icons[normalizedName] || icons.money

  return (
    <span className={className} style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
      {icon}
    </span>
  )
}

// getIconName과 availableIcons를 재export
export { getIconName, availableIcons } from './IconMapper'

