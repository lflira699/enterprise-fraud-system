export type SystemHealthComponent = {
  name: string
  status: string
}

export type SystemHealth = {
  status: string
  informationStatus: string
  components: SystemHealthComponent[]
  checkedAt: string
}
