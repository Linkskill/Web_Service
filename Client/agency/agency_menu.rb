require 'tty-prompt'
require_relative '../modules/web_service.rb'
require_relative '../modules/validate_user_entry.rb'
require_relative '../entities/ticket_entity.rb'
require_relative '../entities/accommodation_entity.rb'

# Menu dos vendedores da agência.
# Vendedores podem cadastrar e excluir passagens e vagas de hospedagem.
class AgencyMenu
    def initialize
        @prompt = TTY::Prompt.new
    end

    # Exibe a tela de menu e as opções
    def display_menu
        while true
            @prompt.say("\n\t→Menu do Vendedor←")
            chosen_option = @prompt.select("Selecione uma opção:", echo: false) do |menu|
                menu.enum '.'
                menu.choice 'Cadastrar passagem', :create_ticket
                menu.choice 'Cadastrar vaga de hospedagem', :create_accommodation
                menu.choice 'Excluir passagem', :delete_ticket
                menu.choice 'Excluir vaga de hospedagem', :delete_accommodation
                menu.choice 'Testar conexão', :test_connection
                menu.choice 'Voltar', :back
            end

            case chosen_option
            when :create_ticket
                create_ticket
            when :create_accommodation
                create_accommodation
            when :delete_ticket
                delete_ticket
            when :delete_accommodation
                delete_accommodation
            when :test_connection
                test_webservice
            when :back
                return
            end
        end
    end

    private
        # Cria e publica novas passagens de acordo com a entrada do usuário
        def create_ticket
            @prompt.say("Preencha os campos a seguir para publicar uma nova passagem.")

            ticket_type = @prompt.select("Tipo da passagem: ", TicketEntity::TYPES)
            origin = @prompt.ask("Local de origem: ", convert: :string) do |e|
                e.required(true, 'Campo vazio, preencha')
            end
            destination = @prompt.ask("Local de destino: ", convert: :string) do |e|
                e.required(true, 'Campo vazio, preencha')
            end
            num_people = @prompt.ask("Número de pessoas: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end
            departure_date = @prompt.ask("Data de partida(dia-mês-ano): ", convert: :date) do |e|
                e.required(true, 'Campo vazio, preencha')
                e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formato "dia-mês-ano"')
            end
            return_date = ticket_type == "ida e volta" ?
                @prompt.ask("Data de retorno(dia-mês-ano): ", convert: :date) do |e|
                    e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formato "dia-mês-ano"')
                end : nil
            price = @prompt.ask("Preço: ", convert: :float) do |e|
                e.required true
                e.validate(ValidateUserEntry::FLOAT_REGEX, 'Insira no formato "0000.00"')
            end

            new_ticket = TicketEntity.new(
                ticket_type: ticket_type,
                origin: origin,
                destination: destination,
                departure_date: departure_date,
                return_date: return_date,
                num_people: num_people,
                price: price
            )

            begin
                response = WebService.publish_ticket(new_ticket)
                @prompt.ok(response)
            rescue => e
                @prompt.error(e.message)
            end
        end

        # Publica uma nova vaga de hospedagem de acordo com a entrada do usuário
        def create_accommodation
            @prompt.say("Preencha os campos a seguir para publicar uma nova vaga de hospedagem.")

            location = @prompt.ask("Nome do local: ", convert: :string) do |e|
                e.required(true, 'Campo vazio, preencha')
            end
            hotel_name = @prompt.ask("Nome do hotel: ", convert: :string) do |e|
                e.required(true, 'Campo vazio, preencha')
            end
            num_rooms = @prompt.ask("Número de quartos: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end
            num_people = @prompt.ask("Número de pessoas: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end
            entrance_date = @prompt.ask("Data de entrada(dia-mês-ano): ", convert: :date) do |e|
                e.required(true, 'Campo vazio, preencha')
                e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formato "dia-mês-ano"')
            end
            exit_date = @prompt.ask("Data de saída(dia-mês-ano): ", convert: :date) do |e|
                e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formato "dia-mês-ano"')
            end
            price = @prompt.ask("Preço: ", convert: :float) do |e|
                e.required true
                e.validate(ValidateUserEntry::FLOAT_REGEX, 'Insira no formato "0000.00"')
            end

            new_accommodation = AccommodationEntity.new(
                location: location,
                hotel_name: hotel_name,
                entrance_date: entrance_date,
                exit_date: exit_date,
                num_rooms: num_rooms,
                num_people: num_people,
                price: price
            )

            begin
                response = WebService.publish_accommodation(new_accommodation)
                @prompt.ok(response)
            rescue => e
                @prompt.error(e.message)
            end
        end

        # Deleta a passagem de acordo com ID(identificador) fornecido pelo usuário
        def delete_ticket
            @prompt.say("Preencha os campos a seguir para deletar uma passagem.")

            ticket_id = @prompt.ask("ID da passagem: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end

            confirmation = @prompt.yes?("Deseja excluir a passagem ##{ticket_id}?")
            if confirmation == false
                @prompt.warn("Operação cancelada")
                return
            end

            begin
                response = WebService.delete_ticket(ticket_id)
                @prompt.ok(response)
            rescue => e
                @prompt.error(e.message)
            end
        end

        # Deleta a hospedagem de acordo com o ID(identificador) fornecido pelo usuário
        def delete_accommodation
            @prompt.say("Preencha os campos a seguir para deletar uma vaga de hospedagem.")

            accommodation_id = @prompt.ask("ID da vaga: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end

            confirmation = @prompt.yes?("Deseja excluir a vaga ##{accommodation_id}?")
            if confirmation == false
                @prompt.warn("Operação cancelada")
                return
            end

            begin
                response = WebService.delete_accommodation(accommodation_id)
                @prompt.ok(response)
            rescue => e
                @prompt.error(e.message)
                end
        end

        # Verifica se o servidor está respondendo
        def test_webservice
            connection_working = WebService.test

            if connection_working
                @prompt.ok("O servidor está funcionando.")
            else
                @prompt.error("O servidor não esta respondendo.")
            end
        end  

end
