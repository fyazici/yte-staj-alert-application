import React, { Component } from "react"
import { Form, Row, Col, Button, InputGroup, Container, Alert } from "react-bootstrap"
import axios from "axios"
import { withTranslation } from "react-i18next";

class AlertInput extends Component {

    constructor(props) {
        super(props);
        this.state = {
            validated: false,
            alertName: "",
            alertURL: "",
            httpMethod: "GET",
            controlPeriod: 1,
            saveStatusText: "",
            saveStatusVariant: "",
            saveStatusOverlayShown: false
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
        var form = this.refs.AlertInputForm;
        if (form.checkValidity()) {
            axios.post("http://localhost:8080/alerts", {
                alertName: this.state.alertName,
                alertURL: this.state.alertURL,
                httpMethod: this.state.httpMethod,
                controlPeriod: this.state.controlPeriod
            }).then((resp) => {
                this.setState({ saveStatusText: "alertinput.status.success", saveStatusVariant: "success", saveStatusOverlayShown: true });
            }).catch((err) => {
                this.setState({ saveStatusText: "alertinput.status.fail-backend", saveStatusVariant: "danger", saveStatusOverlayShown: true });
                console.error("Alert save error: " + err);
            })
        }
        this.setState({ validated: true });
    }

    render() {
        const { t } = this.props;
        return (
            <Container>
                <Form noValidate validated={this.state.validated} className="AlertInputBox" ref="AlertInputForm">
                    <Alert
                        show={this.state.saveStatusOverlayShown}
                        variant={this.state.saveStatusVariant}
                        dismissible={true}
                        onClose={() => this.setState({ saveStatusOverlayShown: false })}>
                        <Col>
                            {t(this.state.saveStatusText)}
                        </Col>
                    </Alert>
                    <Form.Group as={Row}>
                        <Form.Label column sm={4}>{t("alertinput.name.label")}</Form.Label>
                        <Col>
                            <Form.Control type="text" required placeholder={t("alertinput.name.placeholder")} value={this.state.alertName} onChange={this.handleAlertNameChange} />
                            <Form.Control.Feedback type="invalid">
                                {t("alertinput.name.invalid")}
                            </Form.Control.Feedback>
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row}>
                        <Form.Label column sm={4}>{t("alertinput.url.label")}</Form.Label>
                        <Col>
                            <Form.Control type="url" required placeholder={t("alertinput.url.placeholder")} value={this.state.alertURL} onChange={this.handleAlertURLChange} />
                            <Form.Control.Feedback type="invalid">
                            {t("alertinput.url.invalid")}
                            </Form.Control.Feedback>
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row}>
                        <Form.Label column sm={4}>{t("alertinput.httpmethod.label")}</Form.Label>
                        <Col>
                            <Form.Control as="select" value={this.state.httpMethod} onChange={this.handleHTTPMethodChange}>
                                <option>GET</option>
                                <option>POST</option>
                            </Form.Control>
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row}>
                        <Form.Label column sm={4}>{t("alertinput.controlperiod.label")}</Form.Label>
                        <Col>
                            <InputGroup>
                                <Form.Control type="number" min="1" value={this.state.controlPeriod} onChange={this.handleControlPeriodChange} />
                                <InputGroup.Append>
                                    <InputGroup.Text>{t("alertinput.controlperiod.seconds-unit")}</InputGroup.Text>
                                </InputGroup.Append>
                            </InputGroup>
                        </Col>
                    </Form.Group>
                    <Button variant="primary" size="md" block onClick={this.handleAlertSave}>{t("alertinput.save-button")}</Button>
                </Form>
            </Container>
        );
    }
}

export default withTranslation()(AlertInput);