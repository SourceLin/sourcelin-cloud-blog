import { getStorage, setStorage } from '@/utils/storage';

const USER_PREFERENCES_KEY = 'app.user.preferences';

export interface UserPreferences {
  analyticsEnabled: boolean;
  personalizedRecommendationEnabled: boolean;
  nightReadingEnabled: boolean;
}

const DEFAULT_PREFERENCES: UserPreferences = {
  analyticsEnabled: true,
  personalizedRecommendationEnabled: true,
  nightReadingEnabled: false
};

export function getUserPreferences(): UserPreferences {
  const stored = getStorage<Partial<UserPreferences> | null>(USER_PREFERENCES_KEY, null);
  return {
    ...DEFAULT_PREFERENCES,
    ...(stored || {})
  };
}

export function updateUserPreferences(patch: Partial<UserPreferences>): UserPreferences {
  const next = {
    ...getUserPreferences(),
    ...patch
  };
  setStorage(USER_PREFERENCES_KEY, next, 60 * 60 * 24 * 365);
  return next;
}
