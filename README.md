![Header](docs/assets/github-header-image.png)

# Stable-Diffusion-Android

![Google Play](https://img.shields.io/endpoint?color=blue&logo=google-play&logoColor=white&url=https%3A%2F%2Fplay.cuzi.workers.dev%2Fplay%3Fi%3Dcom.shifthackz.aisdv1.app%26l%3DGoogle%2520Play%26m%3D%24version)
![F-Droid](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Ff-droid.org%2Fapi%2Fv1%2Fpackages%2Fcom.shifthackz.aisdv1.app.foss&query=%24.packages%5B0%5D.versionName&label=F-Droid&link=https%3A%2F%2Ff-droid.org%2Fpackages%2Fcom.shifthackz.aisdv1.app.foss%2F)


[![Google Play](docs/assets/google_play.png)](https://play.google.com/store/apps/details?id=com.shifthackz.aisdv1.app)
[![F-Droid](docs/assets/fdroid.png)](https://f-droid.org/packages/com.shifthackz.aisdv1.app.foss)

Stable Diffusion AI is an easy-to-use app that lets you quickly generate images from text or other images with just a few clicks. With this app, you can communicate with your own server and generate high-quality images in seconds.

## Features

- Can use server environment powered by [AI Horde](https://stablehorde.net/) (a crowdsourced distributed cluster of Stable Diffusion workers)
- Can use server environment powered by [Stable-Diffusion-WebUI](https://github.com/AUTOMATIC1111/stable-diffusion-webui) (AUTOMATIC1111)
- Supports original Txt2Img, Img2Img modes
  - **Positive** and **negative** prompt support
  - Support dynamic **size** in range from 64 to 2048 px (for width and height)
  - Selection of different **sampling methods** (available samplers are loaded from server)
  - Unique **seed** input
  - Dynamic **sampling steps** in range from 1 to 150
  - Dynamic **CFG scale** in range from 1.0 to 30.0
  - **Restore faces** option
  - ( Img2Img ONLY ) : Image selection from device gallery _(requires user permission)_
  - ( Img2Img ONLY ) : Capture input image from camera _(requires user permission)_
  - ( Img2Img ONLY ) : Fetching random image for the input 
- In-app Gallery, stored locally, contains all AI generated images
  - Displays generated images grid
  - Image detail view: Zoom, Pinch, Generation Info. 
  - Export all gallery to **.zip** file
  - Export single photo to **.zip** file
- Settings
  - WebUI server URL
  - Active SD Model selection
  - Server availability monitoring (http-ping method)
  - Enable/Disable auto-saving of generated images
  - Clear gallery / app cache

## Setup instruction

### Option 1: Use your own Automatic1111 instance

This requires you to have the AUTOMATIC1111 WebUI that is running in server mode.

You can have it running either on your own hardware with modern GPU from Nvidia or AMD, or running it using Google Colab. 

1. Follow the setup instructions on [Stable-Diffusion-WebUI](https://github.com/AUTOMATIC1111/stable-diffusion-webui) repository.
2. Add the arguments `--api --listen` to the command line arguments of WebUI launch script.
3. After running the server, get the IP address, or URL of your WebUI server.
4. On the first launch, app will ask you for the server URL, enter it and press Connect button. If you want to change the server URL, go to Settings tab, choose Configure option, and repeat the setup flow.

If for some reason you have no ability to run your server instance, you can toggle the **Demo mode** swith on server setup page: it will allow you to test the app and get familiar with it, but it will return some mock images instead of AI-generated ones.

### Option 2: Use AI Horde

[AI Horde](https://stablehorde.net/) is a crowdsourced distributed cluster of Image generation workers and text generation workers. 

AI Horde requires to use API KEY, this mobile app alows to use either default API KEY (which is "0000000000"), or type your own. You can sign up and get your own AI Horde API KEY [here](https://stablehorde.net/register).

## Supported languages

App uses the language provided by OS default settings.

User interface of the app is translated for languages listed in this table:

| Language | Since version | Status |
| --- | --- | --- |
| English | 0.1.0 | `Translated` |
| Ukrainian | 0.1.0 | `Translated` |
| Turkish | 0.4.1 | `Translated` |

Any contributions to the translations are welcome.

## Donate

This software is open source, provided with no warranty, and you are welcome to use it for free. 

In case you find this software valuable, and you'd like to say thanks and show a little support, here is the button:

[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/shifthackz)
