import React, {Component} from "react"
import {Form, Row, Col, Button} from "react-bootstrap"
import axios from "axios"

class AlertInput extends Component {

    constructor(props) {
        super(props);
        this.state = {
            alertName: "", 
            alertURL: "",
            httpMethod: "",
            controlPeriod: 1
        };
    }

    handleAlertNameChange = (event) => {
        this.setState({alertName: event.target.value});
        console.log(event.target.value);
    }

    handleAlertURLChange = (event) => {
        this.setState({alertURL: event.target.value});
        console.log(event.target.value);
    }

    handleHTTPMethodChange = (event) => {
        this.setState({httpMethod: event.target.value});
        console.log(event.target.value);
    }

    handleControlPeriodChange = (event) => {
        this.setState({controlPeriod: event.target.value});
        console.log(event.target.value);
    }

    handleAlertSave = () => {
        axios.post("http://localhost:8090/alerts", {
            name: this.state.alertName,
            url: this.state.alertURL,
            method: this.state.httpMethod,
            period: this.state.controlPeriod
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
                            <Form.Control type="text" value={this.state.alertName} onChange={this.handleAlertNameChange}/>
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row}>
                        <Form.Label column sm={4}>URL:</Form.Label>
                        <Col>
                            <Form.Control type="text" value={this.state.alertURL} onChange={this.handleAlertURLChange}/>
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
                            <Form.Control type="number" min="1" value={this.state.controlPeriod} onChange={this.handleControlPeriodChange} />
                        </Col>
                    </Form.Group>
                    <Button variant="primary" size="md" block onClick={this.handleAlertSave}>Kaydet</Button>
                </Form>
            </div>
        );
    }
}

export default AlertInput;