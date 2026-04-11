# Qwen Code Plugin for Apache NetBeans

> **Copilot-like AI-ассистент** для Apache NetBeans. Inline-подсказки кода по `Tab`, контекстное меню с AI-действиями, агент, консоль-логгер и панель настроек — всё работает **локально** через `qwen-code-cli`, без облачного API.

---

## Возможности

### ⚡ Inline-подсказки кода (как GitHub Copilot)

1. **Начните писать код** — остановитесь на секунду
2. **Появится серая подсказка** (ghost text) — AI предлагает продолжение
3. **Нажмите `Tab`** — вставить предложение
4. **Нажмите `Escape`** — отменить
5. **Продолжайте печатать** — подсказка исчезнет и появится новая

### 🧠 5 AI-действий с кодом

Выделите код → правый клик:

| Действие | Описание |
|---|---|
| **Qwen: Explain Code** | Объясняет выделенный код построчно |
| **Qwen: Generate Code** | Генерирует код по описанию |
| **Qwen: Refactor / Optimize** | Улучшает читаемость и производительность |
| **Qwen: Write Unit Tests** | Создаёт unit-тесты |
| **Qwen: Find Vulnerabilities** | Ищет уязвимости и ошибки |

### 📋 Меню и Toolbar

После установки в строке меню появится пункт **Qwen AI** с 7 действиями, а в панели инструментов — **Qwen AI Toolbar** с кнопками.

### 💬 Консоль и логирование

Окно **Qwen AI Console** — интерактивный чат и логер всех действий плагина:
- Вывод CLI, ошибки, статус сессий
- Команды: `/clear`, `/stats`, `/compress`, `/help`
- Свободный ввод текста → отправка в CLI

Открыть: меню **Qwen AI → Qwen: Open Console**

### ⚙️ Настройки

`Tools → Options → Qwen AI`:
| Параметр | Описание | По умолчанию |
|---|---|---|
| Path to qwen-code-cli | Путь к CLI или имя в PATH | автопоиск |
| Model | Модель CLI (`--model`) | `qwen-coder` |
| Timeout (seconds) | Макс. время ожидания | 120 |

---

## Системные требования

| Компонент | Версия | Примечание |
|---|---|---|
| **JDK** | 25+ | Совместима с NetBeans |
| **Apache NetBeans** | 28+ | IDE для установки плагина |
| **Node.js** | 18+ | Требуется для qwen-code-cli |
| **qwen-code-cli** | последняя | AI-инструмент CLI |

---

## Установка зависимостей

### 1. qwen-code-cli

```bash
# Вариант A: через npm
npm install -g @qwen-code/qwen-code

# Вариант B: через Ollama
ollama pull qwen:code

# Проверка
qwen-code --version
```

### 2. Node.js

```bash
node --version
```

---

## Сборка из исходного кода

```bash
git clone https://github.com/isalnikov/QwenIntegration.git
cd QwenIntegration
mvn clean package
```

Результат: `target/qwen-code-plugin-1.0.0.nbm` (≈ 48 KB)

---

## Установка плагина в NetBeans

1. Откройте **Tools → Plugins**
2. Вкладка **Downloaded** → **Add Plugins...**
3. Выберите `qwen-code-plugin-1.0.0.nbm`
4. Нажмите **Install** и перезапустите IDE

> При первом запуске плагин проверит наличие `qwen-code-cli`. Если не найдёт — покажет предупреждение с инструкцией.

---

## Использование

### Базовый сценарий: объяснить код

1. Откройте файл с кодом (Java, Python, JavaScript и др.)
2. Выделите участок кода
3. Правый клик → **Qwen: Explain Code**
4. Ответ появится в панели вывода **Qwen AI**

### Вставить результат в код

Ответы CLI отображаются в панели **Qwen AI** окна Output. Скопируйте и вставьте вручную.

### Inline-подсказка (Copilot)

1. Начните писать код
2. Остановитесь на ~500ms
3. Появится серый ghost-текст
4. `Tab` — принять, `Escape` — отменить

### Консоль

1. **Qwen AI → Qwen: Open Console**
2. Введите сообщение → Enter
3. Вывод появится в консоли
4. Команды: `/help`, `/clear`, `/stats`, `/compress`

---

## Архитектура

```
qwen-netbeans-plugin/ (1 модуль, 17 source-файлов)
│
├── PluginInstaller.java          # Стартовая регистрация + проверка CLI
│
├── core/                         ← ЯДРО
│   ├── QwenCliClient.java        # ProcessBuilder: sync + async вызов CLI
│   ├── QwenCliDetector.java      # Проверка наличия CLI и Node.js
│   ├── QwenLogger.java           # Централизованный логгер с ConsoleSink
│   ├── QwenPreferences.java      # Настройки (Preferences API)
│   └── QwenSession.java          # Управление сессией: токены, сообщения
│
├── ui/                           ← ИНТЕРФЕЙС
│   ├── QwenAction.java           # 7 Action-классов (меню, toolbar, контекст)
│   ├── QwenConsoleTopComponent.java # Окно консоли + логер
│   ├── QwenOptionsController.java   # Tools → Options → Qwen AI
│   └── QwenOptionsPanel.java        # Swing-панель настроек
│
├── editor/                       ← РЕДАКТОР
│   ├── QwenInlineCompletion.java # Copilot-like ghost text (Tab accept)
│   └── QwenDiffApplier.java      # Извлечение code-блоков из ответов
│
└── resources/
    ├── Bundle.properties         # i18n строки
    └── layer.xml                 # Регистрация Actions, Menu, Toolbar, Popup
```

### Регистрация действий в NetBeans

```
Actions/Qwen/           ← .instance файлы действий
  ↓ .shadow
Menu/QwenAI/            ← пункт в строке меню
  ↓ .shadow
Toolbars/QwenAI/        ← панель инструментов
  ↓ .shadow
Editors/Popup/          ← контекстное меню редактора
```

Это стандартный паттерн NetBeans Platform — один раз регистрируешь Action в `Actions/`, затем используешь `.shadow` в любых меню.

---

## Тесты

```bash
mvn test
```

| Тест | Что проверяет |
|---|---|
| `QwenDiffApplierTest` (7 тестов) | Извлечение code-блоков: с языком, без, мультисайн, null, пустой, незакрытый |
| `QwenSessionTest` (7 тестов) | Токены, сообщения, clear, stats, active/inactive, ID |

Результат: **14 тестов, 0 ошибок, 0 падений**

---

## Формат ответов CLI

| Формат | Обработка |
|---|---|
| Обычный текст | Выводится в панель Qwen AI и консоль |
| Code block (` ``` `) | Извлекается `QwenDiffApplier` |
| Любой ответ | Логируется в консоль с меткой `[INFO]`, `[ERROR]`, `[WARNING]` |

---

## Устранение проблем

| Проблема | Решение |
|---|---|
| **Нет пункта Qwen AI в меню** | Проверьте, что плагин установлен: Tools → Plugins → Installed |
| **Qwen Code CLI not found** | Установите: `npm install -g @qwen-code/qwen-code` или укажите путь в настройках |
| **Нет inline-подсказок** | Убедитесь, что CLI работает и отвечает; проверьте консоль на ошибки |
| **Таймаут** | Увеличьте Timeout в Tools → Options → Qwen AI |
| **Ghost text не появляется** | Проверьте консоль Qwen AI Console — там будут логи ошибок CLI |
| **Нет панели Qwen AI** | Установите плагин заново и перезапустите IDE |

---

## Разработка

### Добавить новое действие

1. В `QwenAction.java` добавьте класс:
```java
public static class MyAction extends QwenAction {
    public MyAction() { super("Qwen: My Action"); }
    @Override protected String getPrompt() { return "my prompt"; }
}
```
2. В `Bundle.properties` добавьте ключ
3. В `layer.xml` добавьте `.instance` в `Actions/Qwen/` и `.shadow` в нужные меню

### Изменить промпт

Отредактируйте `buildInput()` в `QwenCliClient.java`.

---

## Лицензия

Apache License, Version 2.0

---

## Автор

Igor Salnikov

---

## Ссылки

- [Apache NetBeans](https://netbeans.apache.org/)
- [qwen-code на GitHub](https://github.com/QwenLM/qwen-code)
- [NetBeans NBM Maven Plugin](https://netbeans.apache.org/)
