# Code Red Robotics Team 5567 The First Responders 

## 2020 Robot Code

### Code standard naming convention notes:
* Member variables should be pre-pended with m_ and utilize camelCasing
* Static non-constant variables should use lower_snake_case_formatting
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

### Non-Motor Can Port Numbers
| Device Name   |  Port Number  |
| ------------- | ------------- |
| PCM           | 20            |
| PDP           | 0             |

### Current Robot Motor Layout
| System         | Motor         | Controller  | Count   | Location   | CAN ID 
| -------------- | ------------- | ----------- | ------- | ---------- | -------------- 
| Drive train    | Falcon 500    | Talon FX    |    4    | Built-in   | MasterLeft: 3
|                |               |             |         |            | MasterRight: 4
|                |               |             |         |            | SlaveLeft: 13
|                |               |             |         |            | SlaveRight: 14
| Shooter        | 775 Pro       | Talon SRX   |    1    | On Shooter | 21
|                |               | Victor SPX  |    3    | Belly Pan  | CLose: 22
|                |               |             |         |            | Far: 23, 24
| Outer Intake   | Neo           | Spark Max   |    1    | Belly Pan  | N/A: PWM 0
| Inner Intake   | 775 Pro       | Victor SPX  |    1    | Belly Pan  | 16
| Indexer/Loader | 775 Pro       | Talon SRX   |    1    | On Indexer | 26
| Climber Deploy | 775 Pro       | Talon SRX   |    1    | On Climber | 27
| Climber Winch  | Neo           | Spark Max   |    1    | Belly Pan  | N/A: PWM 1

##LimelightPipelines
* This folder contains pipelines used on the Limelight. 
* If any changes are made to those pipelines, those changes should be put in this folder to pull into master
* The Driver p3 and StandardZoom p0 pipelines are the pipelines currently used in the code