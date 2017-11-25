export const DISMISS_INTRO = 'DISMISS_INTRO';

export function dismissIntro() {
  return {
    type: DISMISS_INTRO
  }
}

export const DISMISS_INSTRUCTION = 'DISMISS_INSTRUCTION';

export function dismissInstruction(id) {
  return {
    type: DISMISS_INSTRUCTION,
    id
  }
}