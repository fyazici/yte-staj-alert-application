import React, { Component } from "react"
import { Container, Button, Accordion, Table, Card } from "react-bootstrap";
import axios from "axios"

class AlertList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            alerts: [
                { alertId: 301, alertName: "Google Status", alertURL: "http://www.google.com", httpMethod: "get", controlPeriod: 1 },
                { alertId: 302, alertName: "Facebook Status", alertURL: "http://www.facebook.com", httpMethod: "get", controlPeriod: 1 },
            ]
        };

        console.log(props);
    }

    handleAlertList = () => {
        axios.get(
            "http://localhost:8080/alerts"
        ).then((resp) => {
            this.setState({ alerts: resp.data });
        }).catch((err) => {
            console.error("handleAlertList failed: " + err);
        });
    }

    handleAlertSelectionChange = (index) => {
        this.props.onAlertSelectionChange(this.state.alerts[index].alertId);
    }

    render() {
        var alertCards = "";
        if (this.state.alerts) {
            alertCards = this.state.alerts.map((elem, index) => {
                return (
                    <Card key={index}>
                        <Accordion.Toggle
                            as={Card.Header}
                            eventKey={index}
                            onClick={() => { this.handleAlertSelectionChange(index); }}
                        >
                            <code>#{index + 1}</code> {elem.alertName}
                        </Accordion.Toggle>
                        <Accordion.Collapse eventKey={index}>
                            <Card.Body>
                                <Table bordered striped hover>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Alarm URL</th>
                                            <th>HTTP Metodu</th>
                                            <th>Kontrol Periyodu</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>{elem.alertId}</td>
                                            <td>{elem.alertURL}</td>
                                            <td>{elem.httpMethod}</td>
                                            <td>{elem.controlPeriod}</td>
                                        </tr>
                                    </tbody>
                                </Table>
                            </Card.Body>
                        </Accordion.Collapse>
                    </Card>
                )
            })
        }

        return (
            <Container className="AlertListBox">
                <Container className="AlertListViewBox">
                    <Accordion>
                        {alertCards}
                    </Accordion>
                </Container>
                <Button variant="primary" size="md" block onClick={this.handleAlertList}>Listele</Button>
            </Container>
        );
    }
}

export default AlertList;