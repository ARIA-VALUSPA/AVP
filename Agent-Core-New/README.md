# ARIA-Dialogue Management
The dialogue manager used inthe ARIA-VALUSPA project, based on the Flipper DM developed at University of Twente.

## Setup
By default the Dialogue manager will use an ActiveMQ instance running on localhost:61616. You can change this on the config/managers.xml file.

There are two different body (information retrieval) modules available. These can be changed by changing both the IntentGenerator and BehaviourGenerator Managers found in the same managers.xml file.

## Content

The Dialogue Manager exists of four parts:

1. Hello Sequence
2. Information Retrieval (A and B - one is used at a time)
3. Goodbye Sequence

### Hello Sequence
The dialogue starts when eMax reports that a face was found. Alice will start talking to you and the conversation begins, there is some freedom, and regardless of what you say, in the end you will always end up in the Information Retrieval. (Unless Alice or you decided to close the conversation)


### Information Retrieval
In Information Retrieval A (info-responses.xml & info-rules.xml) you can ask questions and follow up questions about Mad Hatter, White Rabbit and Gryphon. it save more information about the conversation (current subjects, visited subjects).

In B you there is a wider variety of subjects to ask about and a more respones react on the user behaviour. It is heavly based on the previous DM, and uses less differenct FMLTemplates.

No actual information retrieval is done at the time of writing.

### Goodbye Sequence
The Goodbye sequence is initated by the Agent or User. The user can start it by saying "goodbye" or similar. If by mistake the agent understands bye, the user can cancel this by saying "No not yet", or similar.

The Agent will also close the conversation based on several rules:
* The user is no longer reported by eMax 
* The user's interest is low and several turns have passed.
* 120 seconds have passed.

After this the DM will reset itself and the conversation can be done again.