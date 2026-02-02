import type { Metadata } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: '가계부',
  description: '가계부 관리 애플리케이션',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  )
}

