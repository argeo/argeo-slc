amq.sendMessage('topic://agent.c6fb85cc-200e-41f1-9b63-fade5cad0f14.newExecution','<slc:slc-execution uuid="b0b68669-b598-4518-8ae3-c9c3190e87b4"><slc:status>STARTED</slc:status><slc:type>slcAnt</slc:type><slc:host>localhost</slc:host><slc:user>user</slc:user><slc:attributes><slc:attribute name="ant.file">/test</slc:attribute></slc:attributes></slc:slc-execution>');
alert('Message sent!');
document.write('Message sent');