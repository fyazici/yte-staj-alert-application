import React, { Component } from "react"
import { Container, Button, Accordion, Table, Card } from "react-bootstrap";
import axios from "axios"
import { Link } from "react-router-dom"

class AlertList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            alerts: []
        };
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

    componentDidUpdate(prevProps, prevState) {
        if (this.props.alertInputState !== prevProps.alertInputState) {
            this.handleAlertList();
        }
    }

    componentDidMount() {
        this.handleAlertList();
    }

    render() {
        var alertCards = "";
        if (this.state.alerts) {
            alertCards = this.state.alerts.map((elem, index) => {
                return (
                    <Card key={index}>
                        <Accordion.Toggle
                            as={Card.Header}
                            eventKey={index}>
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
                                            <td><Link to={"/alerts/" + elem.alertId}>{elem.alertId}</Link></td>
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