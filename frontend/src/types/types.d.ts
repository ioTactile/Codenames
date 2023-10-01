export type Room = {
  id: string
  players: Player[]
  words: Word[]
  clues: Clue[]
  teamTurn: 'BLUE' | 'RED'
  status: 'NEW' | 'PENDING' | 'IN_PROGRESS' | 'RED_TEAM_WINS' | 'BLUE_TEAM_WINS'
  redReimainingWords: number
  blueReimainingWords: number
  isBlackCard: boolean
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}

export type Player = {
  name: string
  playerTeam: 'NONE' | 'BLUE' | 'RED'
  playerRole: 'NONE' | 'SPYMASTER' | 'OPERATIVE' | 'SPECTATOR'
}

export type Word = {
  wordName: string
  selectedBy: string[]
  wordState: 'NOT_SELECTED' | 'SELECTED' | 'CLICKED'
  wordColor: 'BLUE' | 'RED' | 'BLACK' | 'WHITE'
}

export type Clue = {
  clueName: string
  attemps: number
  remaining: number
  spyName: string
}
