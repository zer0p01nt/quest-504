import { api, getCsrfHeaders } from './client';

export interface StartGameResponse {
  gameId: number;
}

export interface GuessResponse {
  strikes: number;
  balls: number;
  solved: boolean;
}

export interface RewardResponse {
  rewardAmount: number;
}

export async function startGame(): Promise<StartGameResponse> {
  const { data } = await api.post<StartGameResponse>(
    '/games',
    undefined,
    { headers: await getCsrfHeaders() },
  );
  return data;
}

export async function submitGuess(gameId: number, guess: string): Promise<GuessResponse> {
  const { data } = await api.post<GuessResponse>(
    `/games/${gameId}/guesses`,
    { guess },
    { headers: await getCsrfHeaders() },
  );
  return data;
}

export async function claimReward(gameId: number): Promise<RewardResponse> {
  const { data } = await api.post<RewardResponse>(
    `/games/${gameId}/reward`,
    undefined,
    { headers: await getCsrfHeaders() },
  );
  return data;
}
