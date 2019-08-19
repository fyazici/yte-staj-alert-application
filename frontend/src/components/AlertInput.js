import React, { Component } from "react"
import { Form, Row, Col, Button, InputGroup } from "react-bootstrap"
import axios from "axios"


class AlertInput extends Component {

    constructor(props) {
        super(props);
        this.state = {
            alertName: "",
            alertURL: "",
            httpMethod: "GET",
            controlPeriod: 1
        };
    }

    handleAlertNameChange = (event) => {
        this.setState({ alertName: event.target.value });
    }

    handleAlertURLChange = (event) => {
        this.setState({ alertURL: event.target.value });
    }

    handleHTTPMethodChange = (event) => {
        this.setState({ httpMethod: event.target.value });
    }

    handleControlPeriodChange = (event) => {
        this.setState({ controlPeriod: event.target.value });
    }

    handleAlertSave = () => {
        axios.post("http://localhost:8080/alerts", { 
            alertName: this.state.alertName,
            alertURL: this.state.alertURL,
            httpMethod: this.state.httpMethod,
            controlPeriod: this.state.controlPeriod
        }).then((resp) => {
            this.props.onAlertInputStateChange();
        }).catch((err) => {
            console.error("Alert save error: " + err);
        })
    }

    render() {
        return (
            <div>
                <Form className="AlertInputBox">
                    <Form.Group as={Row}>
                        <Form.Label column sm={4}>Adı:</Form.Label>
                        <Col>
                            <Form.Control type="text" placeholder="test.xyz Status" value={this.state.alertName} onChange={this.handleAlertNameChange} />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row}>
                        <Form.Label column sm={4}>URL:</Form.Label>
                        <Col>
                            <Form.Control type="text" placeholder="http://test.xyz" value={this.state.alertURL} onChange={this.handleAlertURLChange} />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row}>
                        <Form.Label column sm={4}>HTTP Metodu:</Form.Label>
                        <Col>
                            <Form.Control as="select" value={this.state.httpMethod} onChange={this.handleHTTPMethodChange}>
                                <option>GET</option>
                                <option>POST</option>
                            </Form.Control>
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row}>
                        <Form.Label column sm={4}>Kontrol Periyodu:</Form.Label>
                        <Col>
                            <InputGroup>
                                <Form.Control type="number" min="1" value={this.state.controlPeriod} onChange={this.handleControlPeriodChange} />
                                <InputGroup.Append>
                                    <InputGroup.Text>sn</InputGroup.Text>
                                </InputGroup.Append>
                            </InputGroup>
                        </Col>
                    </Form.Group>
                    <Button variant="primary" size="md" block onClick={this.handleAlertSave}>Kaydet</Button>
                </Form>
            </div>
        );
    }
}

export default AlertInput;