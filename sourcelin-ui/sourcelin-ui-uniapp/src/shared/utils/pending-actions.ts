import { likeTarget, collectTarget, followUser } from '@/modules/interaction/api/interaction.api';
import { createComment } from '@/modules/comment/api/comment.api';
import type { PendingAction } from '@/utils/auth';

interface TargetPayload {
  targetType: 'article' | 'say' | 'treehole';
  targetId: number;
}

interface FollowPayload {
  targetUserId: number;
}

interface CommentPayload {
  targetId: number;
  source: 'article' | 'say' | 'treehole';
  content: string;
}

export async function replayPendingAction(action: PendingAction): Promise<void> {
  if (!action || !action.payload) return;
  if (action.type === 'interaction:like') {
    const payload = action.payload as TargetPayload;
    await likeTarget(payload.targetType, payload.targetId);
  }
  if (action.type === 'interaction:collect') {
    const payload = action.payload as TargetPayload;
    await collectTarget(payload.targetType, payload.targetId);
  }
  if (action.type === 'user:follow') {
    const payload = action.payload as FollowPayload;
    await followUser(payload.targetUserId);
  }
  if (action.type === 'comment:create') {
    const payload = action.payload as CommentPayload;
    await createComment({
      articleId: payload.targetId,
      source: payload.source,
      content: payload.content
    });
  }
}
