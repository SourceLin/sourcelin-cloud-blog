/**
 * 高性能 Web Animations API 粒子爆破特效
 */
export function spawnBurstParticles(event: MouseEvent, color = 'var(--primary-color)') {
  const target = event.currentTarget as HTMLElement
  if (!target) return
  const rect = target.getBoundingClientRect()
  
  // 创建粒子容器
  const container = document.createElement('div')
  container.style.position = 'fixed'
  container.style.left = `${rect.left + rect.width / 2}px`
  container.style.top = `${rect.top + rect.height / 2}px`
  container.style.pointerEvents = 'none'
  container.style.zIndex = '99999'
  document.body.appendChild(container)

  const count = 10
  for (let i = 0; i < count; i++) {
    const el = document.createElement('div')
    el.style.position = 'absolute'
    
    // 随机微粒大小
    const size = 4 + Math.random() * 4
    el.style.width = `${size}px`
    el.style.height = `${size}px`
    el.style.borderRadius = '50%'
    el.style.background = color
    el.style.transform = 'translate(-50%, -50%)'
    
    // 计算放射角度与距离
    const angle = (i * 360 / count) + (Math.random() - 0.5) * 20
    const radian = (angle * Math.PI) / 180
    const velocity = 35 + Math.random() * 25
    const tx = Math.cos(radian) * velocity
    const ty = Math.sin(radian) * velocity

    // 原生高性能动画接口
    el.animate([
      { transform: 'translate(-50%, -50%) scale(1.2)', opacity: 1 },
      { transform: `translate(calc(-50% + ${tx}px), calc(-50% + ${ty}px)) scale(0)`, opacity: 0 }
    ], {
      duration: 500 + Math.random() * 150,
      easing: 'cubic-bezier(0.1, 0.8, 0.3, 1)',
      fill: 'forwards'
    })

    container.appendChild(el)
  }

  // 延时回收容器
  window.setTimeout(() => container.remove(), 750)
}
