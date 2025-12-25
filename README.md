<div align="center">

<a href="https://mihon.app">
    <img src="./.github/assets/logo.png" alt="Mihon logo" title="Mihon logo" width="80"/>
</a>

# Mihon AI

### Full-featured reader with AI Enhancements
This is a customized fork of [Mihon](https://github.com/mihonapp/mihon) that adds the ability to Enhance manga panels (Color, Translate, Clean) using AI.

[![Discord server](https://img.shields.io/discord/1195734228319617024.svg?label=&labelColor=6A7EC2&color=7389D8&logo=discord&logoColor=FFFFFF)](https://discord.gg/mihon)
<!-- [![GitHub downloads](https://img.shields.io/github/downloads/mihonapp/mihon/total?label=downloads&labelColor=27303D&color=0D1117&logo=github&logoColor=FFFFFF&style=flat)](https://mihon.app/download) -->

[![CI](https://img.shields.io/github/actions/workflow/status/mihonapp/mihon/build.yml?labelColor=27303D)](https://github.com/mihonapp/mihon/actions/workflows/build_push.yml)
[![License: Apache-2.0](https://img.shields.io/github/license/mihonapp/mihon?labelColor=27303D&color=0877d2)](/LICENSE)
[![Translation status](https://img.shields.io/weblate/progress/mihon?labelColor=27303D&color=946300)](https://hosted.weblate.org/engage/mihon/)

## Download

[Download](https://github.com/IBRAHIMIAOB/Mihon-AI/releases/tag/Dev)

<!--
[![Mihon Stable](https://img.shields.io/github/release/mihonapp/mihon.svg?maxAge=3600&label=Stable&labelColor=06599d&color=043b69)](https://mihon.app/download)
[![Mihon Beta](https://img.shields.io/github/v/release/mihonapp/mihon-preview.svg?maxAge=3600&label=Beta&labelColor=2c2c47&color=1c1c39)](https://mihon.app/download)
-->

*Requires Android 8.0 or higher.*

</div>

## Mihon AI Enhancements

This specialized fork of Mihon is dedicated to integrating AI-powered features, primarily focusing on **AI Enhancements** for manga panels.

**Our Goal:** We encourage the community to test, develop, and refine these features so they can eventually be merged into the main [Mihon app](https://github.com/mihonapp/mihon).

### Recommended Models

To get the best results, we recommend using the following Gemini models via OpenRouter. Prices are approximate:

| Model ID | Description | Price (approx.) |
| :--- | :--- | :--- |
| `google/gemini-2.5-flash-image-preview`<br>`google/gemini-2.5-flash-image` | **Cheap & Fast**<br>Good for bulk coloring. | ~$0.03 / image |
| `google/gemini-3-pro-image-preview` | **High Quality**<br>Expensive but provides better detail and coloring. | $0.1 - $1.3 / image |

> [!NOTE]
> For more models and details, visit [OpenRouter Models](https://openrouter.ai/models?fmt=cards&input_modalities=image%2Ctext&output_modalities=image).

### How to Use

To use the AI enhancement features, follow these steps:

1.  **Get an API Key**: Obtain an API key from [OpenRouter](https://openrouter.ai/settings/keys).
2.  **Add Credits**: Ensure you have added credits to your OpenRouter account.
3.  **Configure in App**:
    - Go to **Settings > AI Enhancements**.
    - Enter your **API Key**.
    - Enable **AI Enhancements**.
    - Enter a **Model ID** from the list above.
4.  **Enhancement Methods**:

    **Method A: Automatic Chapter Enhancement (Bulk)**
    - Download a chapter with **AI Enhancements** enabled in settings.
    - The entire chapter will be enhanced during the download process.
    - *Note: This applies coloring to all pages automatically without individual guidelines.*

    **Method B: Individual Panel Enhancement (Guided)**
    - Long-press a panel or open the menu while reading.
    - Select **AI Enhance** for the specific panel.
    - You can provide **guidelines** (scribbles/lines) to direct the AI on how to color specific areas (e.g., hair color, clothes).
    - This allows for higher consistency and control.

### Known Issues
- **Bulk Downloading**: Downloading a whole chapter with AI enabled may not be the best approach for everyone due to cost and varying quality.
- **Image Quality**: Quality can degrade with cheaper/weaker image generation models.
- **Consistency**: Colors may not be consistent from one panel to another (e.g., character hair changing color).

### Features & Roadmap
- **Motion Panels**: We are exploring Image-to-Video to create moving panels. *Currently limited by OpenRouter support.*
- **Color Consistency**: Future updates aim to include a "Color Log" or "Character Profile" system. This would categorize characters (e.g., `Character_Name_SkinColor = White`) to maintain consistent coloring across pages.
- **Contribution**: We welcome all contributions to improve these features!

---

## Original Features

<div align="left">

* Local reading of content.
* A configurable reader with multiple viewers, reading directions and other settings.
* Tracker support: [MyAnimeList](https://myanimelist.net/), [AniList](https://anilist.co/), [Kitsu](https://kitsu.app/), [MangaUpdates](https://mangaupdates.com), [Shikimori](https://shikimori.one), and [Bangumi](https://bgm.tv/) support.
* Categories to organize your library.
* Light and dark themes.
* Schedule updating your library for new chapters.
* Create backups locally to read offline or to your desired cloud service.
* Plus much more...

</div>

## Contributing

[Code of conduct](./CODE_OF_CONDUCT.md) · [Contributing guide](./CONTRIBUTING.md)

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Before reporting a new issue, take a look at the [FAQ](https://mihon.app/docs/faq/general), the [changelog](https://mihon.app/changelogs/) and the already opened [issues](https://github.com/mihonapp/mihon/issues); if you got any questions, join our [Discord server](https://discord.gg/mihon).


### Repositories

[![mihonapp/website - GitHub](https://github-readme-stats.vercel.app/api/pin/?username=mihonapp&repo=website&bg_color=161B22&text_color=c9d1d9&title_color=0877d2&icon_color=0877d2&border_radius=8&hide_border=true&description_lines_count=2)](https://github.com/mihonapp/website/)
[![mihonapp/bitmap.kt - GitHub](https://github-readme-stats.vercel.app/api/pin/?username=mihonapp&repo=bitmap.kt&bg_color=161B22&text_color=c9d1d9&title_color=0877d2&icon_color=0877d2&border_radius=8&hide_border=true&description_lines_count=2)](https://github.com/mihonapp/bitmap.kt/)

### Credits

Thank you to all the people who have contributed!

<a href="https://github.com/mihonapp/mihon/graphs/contributors">
    <img src="https://contrib.rocks/image?repo=mihonapp/mihon" alt="Mihon app contributors" title="Mihon app contributors" width="800"/>
</a>

### Disclaimer

The developer(s) of this application does not have any affiliation with the content providers available, and this application hosts zero content.

### License

<pre>
Copyright © 2015 Javier Tomás
Copyright © 2024 Mihon Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>

</div>
