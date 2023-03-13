![Header](docs/assets/github-header-image.png)

# Stable-Diffusion-Android

[![Version](https://img.shields.io/badge/Version-0.1.0-green)](https://github.com/ShiftHackZ/Stable-Diffusion-Android/releases)


[![Google Play](docs/assets/google_play.png)](https://play.google.com/store/apps/details?id=com.shifthackz.aisdv1.app)

Stable Diffusion AI is an easy-to-use app that lets you quickly generate images from text or other images with just a few clicks. With this app, you can communicate with your own server and generate high-quality images in seconds.

## Features

- Uses server environment powered by [Stable-Diffusion-WebUI](https://github.com/AUTOMATIC1111/stable-diffusion-webui) (AUTOMATIC1111)
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
