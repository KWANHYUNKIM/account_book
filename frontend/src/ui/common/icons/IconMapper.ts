/**
 * 이모지 아이콘을 SVG 아이콘 이름으로 매핑
 */
export const iconMap: Record<string, string> = {
  '💰': 'money',
  '📊': 'chart',
  '💳': 'card',
  '🏦': 'bank',
  '📈': 'trend',
  '💵': 'cash',
  '🎯': 'target',
  '📱': 'phone',
  '✈️': 'plane',
  '🏠': 'home',
  '🍔': 'food',
  '🎬': 'movie',
  '📁': 'folder',
  '⚙️': 'settings',
  '🚪': 'logout',
}

/**
 * 이모지 아이콘 또는 아이콘 이름을 SVG 아이콘 이름으로 변환
 */
export function getIconName(emojiOrName: string): string {
  // 이미 아이콘 이름인 경우 그대로 반환
  if (iconMap[emojiOrName]) {
    return iconMap[emojiOrName]
  }
  // 아이콘 이름이 이미 올바른 경우
  const iconNames = ['money', 'chart', 'card', 'bank', 'trend', 'cash', 'target', 'phone', 'plane', 'home', 'food', 'movie', 'folder', 'settings', 'logout', 'dashboard']
  if (iconNames.includes(emojiOrName)) {
    return emojiOrName
  }
  // 기본값
  return 'money'
}

/**
 * 사용 가능한 모든 아이콘 이름 목록
 */
export const availableIcons = [
  'money',
  'chart',
  'card',
  'bank',
  'trend',
  'cash',
  'target',
  'phone',
  'plane',
  'home',
  'food',
  'movie',
]

