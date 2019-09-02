import React, { Component } from "react"
import { Container, Button, Accordion, Table, Card, Alert } from "react-bootstrap";
import axios from "axios"
import { Link } from "react-router-dom"

class AlertList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            alerts: [],
            showError: false,
            errorMessage: ""
        };
    }

    handleAlertList = () => {
        this.setState({showError: false});
        axios.get(
            "http://localhost:8080/alerts"
        ).then((resp) => {
            this.setState({ alerts: resp.data });
        }).catch((err) => {
            this.setState({ 
                alerts: null,
                showError: true,
                errorMessage: "Alarm listesi getirilemedi!"
            });
            console.error("handleAlertList failed: " + err);
        });
    }

    handleAlertDelete = (alertId) => {
        this.setState({showError: false});
        axios.delete(
            "http://localhost:8080/alert/" + alertId
        ).then((resp) => {
            this.handleAlertList();
        }).catch((err) => {
            this.setState({
                showError: true,
                errorMessage: "Alarm silinemedi!"
            });
            console.error("handleAlertDelete failed: " + err);
        })
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
                                            <th>Kontrol Periyodu (sn)</th>
                                            <th>Sonuçlar</th>
                                            <th>İşlevler</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>{elem.alertId}</td>
                                            <td><a href={elem.alertURL}>{elem.alertURL}</a></td>
                                            <td>{elem.httpMethod}</td>
                                            <td>{elem.controlPeriod}</td>
                                            <td><Link to={"/alerts/" + elem.alertId}><Button variant="info">Sonuçları Göster</Button></Link></td>
                                            <td><Button variant="danger" onClick={() => {
                                                this.handleAlertDelete(elem.alertId);
                                            }}>Sil</Button></td>
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
                    <Alert 
                        show={this.state.showError}
                        variant="danger" 
                        dismissible={true} 
                        onClose={() => { this.setState({showError: false}) }}>
                        {this.state.errorMessage}
                    </Alert>
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