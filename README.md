# RedParallax Viewer

Converts ./input-psd/scene.psd into Scene2D on the fly and renders it.
See example: https://github.com/RedTriplane/RedParallax/releases

![image](https://cloud.githubusercontent.com/assets/1580663/20033859/2f8b47e4-a3ab-11e6-84cd-a578d603fc98.png)

Работает след образом:

1) Надо скачать и распаковать zip.

2) Запустить run.bat.

3) Появится окно в котором будет пример параллакса. Можно шурудить мышкой.

4) В папке input-psd лежит сцена из которой он построен, сцену можно редактировать, если её сохранить, вьювер её автоматически перебилдит и загрузит.

У каждого слоя в параллаксе есть параметы @mX, @mY и @mZ - это умножающие мутипликаторы для настройки сдвига слоя по каждой оси.

Если их нет, то будут учитываться якоря @leftAnchor и @rightAnchor, чтоб эти параметры расчитать автоматом.
Если убрать и якоря, то значения будут дефолтные: 1, 0 и 0.
Из примера всё понятно.

Art by Sergei Ryzhov https://vk.com/ryzhov_art

