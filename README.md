# Code Red Robotics Team 5567 The First Responders 

## 2020 Robot Code

### Code standard naming convention notes:
* Member variables should be pre-pended with m_ and utilize camelCasing
* Constants should use C-style ALL_CAPS_FORMAT
* Class names should always start with a Capital letter
* Method names should always start with a lower-case letter
* Variable names should always start with a lower-case letter

### Current Drive Controls
| System         | Control       |
| -------------- | ------------- |
| Drive Forward  | R. Trigger    |
| Drive Reverse  | L. Trigger    |
| Turn Control   | Left Stick X  |
| Shift Gear     | X Button      |
| Lock-on Target | B Button      |

### Current Can Port Numbers
| Device Name   |  Port Number  |
| ------------- | ------------- |
| PCM           | 20            |
| PDP           | 0             |
| TalonSRX      | 1             |
| TalonSRX      | 2             |
| VictorSPX     | 11            |
| VictorSpx     | 12            |
| VictorSpx     | 15            |

### Current Robot Motor Layout
| System         | Motor         | Controller  | Count   | Location   |
| -------------- | ------------- | ----------- | ------- | ---------- |
| Drive train    | Falcon 500    | Talon FX    |    4    | Built-in   |
| Shooter        | 775 Pro       | Talon SRX   |    1    | On Shooter |
|                |               | Victor SPX  |    3    | Belly Pan  |
| Outer Intake   | Neo           | Spark Max   |    1    | Belly Pan  |
| Inner Intake   | 775 Pro       | Victor SPX  |    1    | Belly Pan  |
| Indexer/Loader | 775 Pro       | Talon SRX   |    1    | On Indexer |
| Climber Deploy | 775 Pro       | Talon SRX   |    1    | On Climber |
| Climber Winch  | Neo           | Spark Max   |    1    | Belly Pan  |
