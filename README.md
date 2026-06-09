# novМузыка

Android-приложение для прослушивания музыки с SoundCloud, Firebase Auth и авторизацией через ВКонтакте.

## Функции

- **Авторизация через Email/пароль** (Firebase Auth)
- **Авторизация через ВКонтакте** (VK OAuth)
- **Восстановление пароля** по email
- **Поиск треков** через SoundCloud API
- **Популярные треки** на главном экране
- **Сохранение треков** в Firebase Firestore
- **Плеер** с ExoPlayer + фоновое воспроизведение
- **Тёмная тема** в фиолетово-чёрных тонах

## Стек

- Kotlin + Jetpack Compose
- Hilt (Dependency Injection)
- Retrofit + OkHttp (API)
- Firebase Auth + Firestore
- ExoPlayer / Media3
- Coil (загрузка изображений)
- Navigation Compose

## Настройка перед запуском

### 1. Firebase

1. Зайди на [Firebase Console](https://console.firebase.google.com/)
2. Создай новый проект
3. Добавь Android-приложение с package name `com.novmusic`
4. Скачай `google-services.json` и положи в папку `app/`
5. Включи в Firebase:
   - **Authentication** → Email/Password
   - **Firestore Database** → создай в режиме test

### 2. SoundCloud API

1. Зайди на [SoundCloud Developers](https://developers.soundcloud.com/)
2. Зарегистрируй приложение
3. Получи **Client ID**

### 3. VK приложение

1. Зайди на [vk.com/apps?act=manage](https://vk.com/apps?act=manage)
2. Создай новое **Standalone**-приложение
3. Получи **App ID**
4. В настройках добавь Redirect URI: `novmusic://vk-callback`

### 4. Конфигурация

Скопируй `local.properties.example` в `local.properties` и заполни:

```properties
SOUNDCLOUD_CLIENT_ID=ваш_client_id
VK_APP_ID=ваш_app_id
```

### 5. Запуск

```bash
# Откройте проект в Android Studio
# Нажмите Run или Shift+F10
```

## GitHub Actions (автосборка APK)

Добавь в Secrets репозитория (`Settings → Secrets → Actions`):

| Secret | Описание |
|--------|----------|
| `GOOGLE_SERVICES_JSON` | Содержимое `google-services.json` (весь JSON) |
| `SOUNDCLOUD_CLIENT_ID` | Client ID из SoundCloud |
| `VK_APP_ID` | App ID из ВКонтакте |
| `KEYSTORE_BASE64` | Keystore в base64 (для Release) |
| `KEYSTORE_PASS` | Пароль keystore |
| `KEY_ALIAS` | Alias ключа |
| `KEY_PASS` | Пароль ключа |

После добавления secrets — каждый push в `main` автоматически собирает APK.

## Firestore структура

```
users/
  {userId}/
    saved_tracks/
      {trackId}/
        id, title, artist, artworkUrl, streamUrl, genre, duration, savedAt
```

## Скриншоты

Главный экран → Популярные треки → Поиск → Избранное → Мини-плеер

## Лицензия

MIT
