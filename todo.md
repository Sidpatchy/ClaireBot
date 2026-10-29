# ClaireBot TODOs — Updated 2025-10-16 18:43

This file replaces an outdated TODO from several release cycles ago. It consolidates current, de-duplicated, prioritized action items discovered across the codebase and docs.

## 1) High‑impact, low‑effort fixes (quick wins)
- Main.java: Do not force-copy default language file each startup.
  - Change saveResource("translations/lang_en-US.yml", true) to false and only copy when missing. Ensure non-en-US locales are handled on first run via lazy copy or locale detection. Priority: P1.
- SlashCommandCreate.java: Remove hardcoded "en-US".
  - Read Main.fallbackLocale or a config param and pass language context appropriately. Priority: P1.
- Translations (lang_en-US.yml, lang_ja-JP.yml, lang_es-ES.yml, lang_TEMPLATE.yml): Replace “# todo insert wiki page”.
  - Insert the correct documentation URL(s) to the wiki/help pages. Priority: P1.
- config.yml: “TODO either automatically add new config file params or remove this.”
  - Decide policy. Prefer auto-merge of new keys at startup; otherwise remove the comment and document manual update. Priority: P1.
- ErrorEmbed.java: “remove these legacy methods.”
  - Identify unused legacy methods, remove or deprecate with @Deprecated and migrate call sites. Priority: P1–P2.

## 2) Internationalization and language flow
- LanguageManager.java
  - Allow server admins to specify a custom language via ClaireData. Extend Guild model + API endpoints to store server language; honor enforceServerLanguage and read from DB (not Discord preferredLocale). Priority: P2.
  - Move locale resolution out of parseUserAndServerOptions into Guild creation or a dedicated locale service; LanguageManager should consume it. Priority: P2.
  - Dependency: ClaireData update (Trello vkQTCTMG). Status: Blocked. Priority: P2 (blocked).

## 3) Command system and registration
- RegisterSlashCommands.java
  - Register commands per-server so server admins may use different languages. Implement per-guild registration with Javacord, mapping guild ID to localized command variants, or use localization hooks if supported. Priority: P2.
- QuoteEmbed.java
  - "validate that this won’t nuke the bot" — add guardrails and error handling; bound rate/size; add a small test or dry-run. Priority: P2.

## 4) Configuration handling
- Main.java
  - “stop using Robin for this. Switch to standard Java classes.” Replace RobinConfiguration for config.yml with Jackson YAML or SnakeYAML + POJO; keep migration path; add validation and defaults. Priority: P3.
- build.gradle
  - Kotlin stdlib re-add when JDK 25 support lands. Track Kotlin/JDK compatibility; once supported, re-enable or rely on transitive stdlib via Kotlin DSL if applicable. Priority: P3 (blocked by external support).

## 5) Documentation
- Writerside/topics/Contributing-guide.md
  - Fill TODO sections: branching model, code style, commit messages, PR checks. Priority: P2.
- Translations wiki links
  - Insert live URLs across all locales (see section 1). Priority: P1.

## 6) Backlog (still relevant)
- Leaderboard
  - Exclude users not in guild; finish final display logic. Monitor race condition issue (#3). Priority: P2.
- Level command
  - API prepared; complete command and listeners; verify race conditions (#3). Priority: P2.
- Points system
  - Make gaining points more robust; define events, caps, anti-abuse. Priority: P3.
- Help command rewrite
  - Use rich, informative help with extended per-command descriptions; point to starters like /config. Priority: P2.
- ClaireLang rollout
  - Collect all language strings into YAML; ensure every command uses LanguageManager; audit placeholders and add tests. Priority: P2.
- ClaireWeb
  - Design website and add ClaireBot API endpoints (in addition to ClaireData) for web queries. Priority: P3.

## Notes and dependencies
- Several i18n items are blocked by ClaireData schema/API changes (see Trello card). Start with unblocked quick wins and documentation.
- Spanish translation file contains natural-language “todo” occurrences (meaning “all/every”); only comment lines at the top were actionable.