# AutoPostSmartTimerTrigger

[ ![Download](https://api.bintray.com/packages/insanusmokrassar/StandardRepository/AutoPostSmartTimerTrigger/images/download.svg) ](https://bintray.com/insanusmokrassar/StandardRepository/AutoPostSmartTimerTrigger/_latestVersion)

## What is it?

It is plugin for `AutoPostTelegramBot` which provide custom triggering
times settings

## How to use it?

In [repository](https://github.com/InsanusMokrassar/AutoPostSmartTimerTrigger)
you can find the [example config](https://github.com/InsanusMokrassar/AutoPostSmartTimerTrigger/blob/master/example.config.json)
with settings. If you want to enable this trigger - just add into plugins list
of your bot:

```json
{
  "plugins": [
    {
      "classname": "com.github.insanusmokrassar.AutoPostSmartTimerTrigger.SmartTimerTriggerPlugin",
      "params": {
        "items": [
          {
            "from": "00:30",
            "to": "01:30",
            "period": "00:30"
          },
          {
            "from": "01:45",
            "to": "06:30",
            "period": "01:30"
          }
        ]
      }
    }
  ]
}
```

Here:

* `from` - included start time of triggering
* `to` - excluded end time of triggering
* `period` - delay between triggering, must be more than `00:00`

All times presented in format `HH:mm` (`HH` - hours, `mm` - minutes).
