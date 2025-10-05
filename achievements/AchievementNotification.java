/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package achievements;

import io.Username;
import util.tuple.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AchievementNotification {
    private static final List<Pair<Username, GameAchievementTemplate>> notifications = new ArrayList<>();
    private static boolean isUpdated = false;
    private static final Object lock = new Object();
    private static Runnable notifier = () -> {};
    private static Username cacheUsername = currentUsername();

    private AchievementNotification() {
        // private constructor to prevent instantiation
    }
    // Username
    /**
     * Return the current username retrieved from {@link GameAchievement} with the {@link GameAchievement#getActiveUser()} method.
     * If there is an error retrieving the username or if the current user is "Guest", this method returns null.
     * <p>
     * The method is safe and does not throw exceptions. It defaults to returning null in case of any issues.
     * <p>
     * The returned result is immediately outdated and should be used as soon as possible.
     * @return the current username, or null if there was an error retrieving it
     */
    private static Username currentUsername() {
        try {
            Username un = GameAchievement.getActiveUser().get();
            if (un.equals("Guest")) {
                return null;
            } else return un;
        } catch (Exception ignored) {
            return null;
        }
    }
    /**
     * Return the cached username, and asynchronously update the cache with the latest username.
     * <p>
     * This method first returns the cached username, which may be outdated. It then starts an
     * asynchronous operation to fetch the latest username from {@link GameAchievement#getActiveUser()}.
     * If the fetched username is "Guest", it sets the cache to null. Otherwise, it updates the cache with the new username.
     * <p>
     * The method is safe and does not throw exceptions. It defaults to returning the cached username in case of any issues.
     * <p>
     * The returned result is already outdated. However, this method does not block and allows the cache to be updated in the background.
     * If it is allowed to block to get the latest username, use {@link #currentUsername()} instead.
     *
     * @return the cached username, which is outdated, or null if it was set to null or not set
     */
    private static Username getUsername() {
        Username cached = cacheUsername;
        Future<Username> future = GameAchievement.getActiveUser();
        CompletableFuture<Username> completableFuture;
        if (future instanceof CompletableFuture) {
            completableFuture = (CompletableFuture<Username>) future;
        } else {
            completableFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return future.get();
                } catch (Exception ignored) {
                    return null;
                }
            });
        }
        completableFuture.thenAccept(un -> {
            if (un.equals("Guest")) un = null;
            if (un == null && cacheUsername != null) {
                // If the user has logged out, clear all notifications
                synchronized (lock) {
                    notifications.clear();
                    isUpdated = true;
                }
            } else if (un != null && !un.equals(cacheUsername)) {
                // If the user has changed, only retain notifications for the new user
                synchronized (lock) {
                    cacheUsername = un;
                    Iterator<Pair<Username, GameAchievementTemplate>> it = notifications.iterator();
                    while (it.hasNext()) {
                        Pair<Username, GameAchievementTemplate> p = it.next();
                        if (!p.getFirst().equals(un)) {
                            it.remove();
                        }
                    }
                    isUpdated = true;
                }
            }
        });
        return cached;
    }

    /**
     * Return whether the achievement notifications have been updated since the last call to {@link #hasNext()}.
     * <p>
     * This method returns true if there are new notifications that have not been checked yet.
     * It does not modify the internal state, so multiple calls to this method will return the
     * same result until {@link #hasNext()} or {@link #size()} is called.
     * <p>
     * The promise of this method is that if this method returns false, then call to {@link #hasNext()} or {@link #size()}
     * is not needed, since no internal structure has changed. In this case, a direct call to {@link #popNotification()}
     * is safe if there are notifications. However, if this method returns true, then {@link #hasNext()} or {@link #size()}
     * must be called to update the internal state before calling {@link #popNotification()}.
     * @return true if there are new notifications, false otherwise
     */
    public static boolean isUpdated() {
        synchronized (lock) {
            return isUpdated;
        }
    }
    /**
     * Return whether there are any achievement notifications available.
     * This method also updates the internal state to indicate that the notifications have been checked.
     * <p>
     * Always call {@link #isUpdated()} before this method, as after calling this method {@link #isUpdated()} should return false.
     * @see #size
     * @return true if there are notifications, false otherwise
     */
    public static boolean hasNext() {
        return size() > 0;
    }
    /**
     * Return the number of achievement notifications available for the current user.
     * This method also updates the internal state to indicate that the notifications have been checked.
     * <p>
     * The returned value of the method is guaranteed to be non-negative. In addition, the method guarantees that,
     * if {@link #isUpdated()} returns false and this method returns a positive number, then {@link #popNotification()} can be safely called
     * that many times without checking {@link #hasNext()} or {@link #isUpdated()} again.
     * <p>
     * Always call {@link #isUpdated()} before this method, as after calling this method {@link #isUpdated()} should return false.
     *
     * @return the number of notifications available
     */
    public static int size() {
        synchronized (lock) {
            Username u = getUsername();
            int count = 0;
            Iterator<Pair<Username, GameAchievementTemplate>> it = notifications.iterator();
            while (it.hasNext()) {
                Pair<Username, GameAchievementTemplate> p = it.next();
                if (p.getFirst().equals(u)) {
                    count++;
                } else {
                    // Remove notifications for other users safely
                    it.remove();
                    // Do not update isUpdated, since this is the hasNext() call
                }
            }
            return count;
        }
    }
    /**
     * Pop the next achievement notification from the queue.
     * This method removes and returns the first notification in the queue.
     * <p>
     * Ideally, this method should only be called if {@link #hasNext()} returns true or {@link #isUpdated()} returns false, otherwise,
     * undefined behavior may occur:
     * <ul>
     *     <li>If there are no notifications available, this method throws an {@link NoSuchElementException}.</li>
     *     <li>If there are notifications for other users but {@link #hasNext()} was not called first,
     *     those notifications may be returned instead of the current user's notifications.</li>
     * </ul>
     * @return a pair containing the username and the achievement template
     * @throws NoSuchElementException if there are no notifications available
     */
    public static Pair<Username, GameAchievementTemplate> popNotification() {
        synchronized (lock) {
            return notifications.removeFirst();
        }
    }
    /**
     * Push a new achievement notification to the queue.
     * This method adds a new notification for the specified user and achievement template.
     * It also updates the internal state to indicate that there are new notifications available.
     * @param user the username associated with the achievement
     * @param achievement the achievement template
     */
    public static void pushNotification(Username user, GameAchievementTemplate achievement) {
        synchronized (lock) {
            notifications.add(new Pair<>(user, achievement));
            isUpdated = true;
            notifier.run();
        }
    }
    /**
     * Push multiple achievement notifications to the queue.
     * This method adds new notifications for the specified user and multiple achievement templates.
     * It also updates the internal state to indicate that there are new notifications available.
     * If no templates are provided, this method does nothing.
     * @param user the username associated with the achievements
     * @param templates the achievement templates
     */
    public static void pushNotifications(Username user, GameAchievementTemplate... templates) {
        synchronized (lock) {
            if (templates.length > 0) {
                for (GameAchievementTemplate t : templates) {
                    notifications.add(new Pair<>(user, t));
                }
                isUpdated = true;
                notifier.run();
            }
        }
    }
    /**
     * Push a new achievement notification to the queue asynchronously.
     * This method adds a new notification for the specified user and achievement template in a separate thread.
     * It also updates the internal state to indicate that there are new notifications available.
     * @param user the username associated with the achievement
     * @param achievement the achievement template
     */
    public static void pushNotificationAsync(Username user, GameAchievementTemplate achievement) {
        CompletableFuture.runAsync(() -> pushNotification(user, achievement));
    }
    /**
     * Push multiple achievement notifications to the queue asynchronously.
     * This method adds new notifications for the specified user and multiple achievement templates in a separate thread.
     * It also updates the internal state to indicate that there are new notifications available.
     * If no templates are provided, this method does nothing.
     * @param user the username associated with the achievements
     * @param templates the achievement templates
     */
    public static void pushNotificationsAsync(Username user, GameAchievementTemplate... templates) {
        CompletableFuture.runAsync(() -> pushNotifications(user, templates));
    }

    /**
     * Hook a notifier that is called whenever a new notification is pushed.
     * The notifier is a {@link Runnable} that is executed in the same thread that calls {@link #pushNotification(Username, GameAchievementTemplate)}
     * or {@link #pushNotifications(Username, GameAchievementTemplate...)}. If the notifier is null, it is treated as a no-op.
     * <p>
     * This method is thread-safe and can be called from any thread. The notifier will be called in the thread that pushes the notification.
     * @param r the notifier to hook
     */
    public static void hookNotifier(Runnable r) {
        synchronized (lock) {
            notifier = r;
        }
    }
}
